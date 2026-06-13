package com.server.app.dto.response;

public record AuthResponse(
        String token,
        Object data
) {
}
