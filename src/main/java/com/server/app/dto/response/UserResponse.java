package com.server.app.dto.response;

import com.server.app.entities.Role;

public record UserResponse(
        Integer id,
        String username,
        String name,
        String surname,
        String email,
        Role role
) {
}
