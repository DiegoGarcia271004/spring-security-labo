package com.server.app.services;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.request.LoginRequest;
import com.server.app.dto.request.UpdatePasswordRequest;
import com.server.app.dto.request.UpdateProfileRequest;
import com.server.app.dto.response.*;
import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final RoleService roleService;
    private final JsonWebToken jsonWebToken;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest req) throws AccessDeniedException {
        User user;
        try {
            user = userService.findByUsername(req.username());
            if (!passwordEncoder.matches(req.password(), user.getPassword())) {
                throw new BadCredentialsException("Credenciales inválidas");
            }
        } catch (NotFoundException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        if (user.isBlocked()) {
            throw new AccessDeniedException("Tu cuenta ha sido bloqueada");
        }

        String token = jsonWebToken.createToken(user);

        return new AuthResponse(token, toProfileResponse(user));
    }

    public AuthResponse signup(UserCreateDto req) {
        if (req.getRole() == null) {
            req.setRole(roleService.getDefaultRole().getId());
        }

        User user = userService.create(req);
        String token = jsonWebToken.createToken(user);
        return new AuthResponse(token, toProfileResponse(user));
    }

    public ProfileResponse getProfile(Integer id) {
        User user = userService.findById(id);
        return toProfileResponse(user);
    }

    public AuthResponse updateProfile(Integer id, UserUpdateDto req) {
        User user = userService.updateUser(id, req);
        String token = jsonWebToken.createToken(user);
        return new AuthResponse(token, toProfileResponse(user));
    }

    public ProfileResponse updatePassword(Integer id, UpdatePasswordRequest req) {
        User user = userService.findById(id);
        if (!passwordEncoder.matches(req.oldpassword(), user.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }

        if (!req.newpassword().equals(req.confirmpassword())) {
            throw new BadCredentialsException("La contraseña y la confirmación deben coincidir");
        }

        User saved = userService.updatePassword(user, req);

        return toProfileResponse(saved);
    }

    private ProfileResponse toProfileResponse(User user) {
        List<PermissionResponse> permissions = user.getRole().getPermissions().stream()
                .map(p -> new PermissionResponse(Math.toIntExact(p.getId()), p.getPath(), p.getMethod()))
                .toList();

        RoleResponse role = new RoleResponse(
                Math.toIntExact(user.getRole().getId()),
                user.getRole().getName(),
                permissions
        );

        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                role
        );
    }
}
