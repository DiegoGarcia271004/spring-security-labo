package com.server.app.controllers;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.request.LoginRequest;
import com.server.app.dto.request.UpdatePasswordRequest;
import com.server.app.dto.request.UpdateProfileRequest;
import com.server.app.dto.response.AuthResponse;
import com.server.app.dto.response.ProfileResponse;
import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.User;
import com.server.app.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) throws AccessDeniedException {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody UserCreateDto req) {
        return ResponseEntity.ok(authService.signup(req));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Integer userId = user.getId();
        return ResponseEntity.ok(authService.getProfile(userId));
    }

    @PutMapping("/update/profile/{id}")
    public ResponseEntity<AuthResponse> updateProfile(@PathVariable Integer id, @RequestBody UserUpdateDto req) {
        return ResponseEntity.ok(authService.updateProfile(id, req));
    }

    @PutMapping("/update/password/{id}")
    public ResponseEntity<ProfileResponse> updatePassword(@PathVariable Integer id, @RequestBody UpdatePasswordRequest req) {
        return ResponseEntity.ok(authService.updatePassword(id, req));
    }
}
