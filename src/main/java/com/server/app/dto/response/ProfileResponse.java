package com.server.app.dto.response;

public record ProfileResponse(
        Integer id,
        String username,
        String name,
        String surname,
        String email,
        RoleResponse role
) {
}
