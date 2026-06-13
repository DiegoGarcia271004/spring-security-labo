package com.server.app.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank
        String username,

        @NotBlank
        String name,

        @NotBlank
        String surname,

        @NotBlank
        String email
) {
}
