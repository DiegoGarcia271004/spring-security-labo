package com.server.app.dto.request;

public record UpdatePasswordRequest(
        String oldpassword,
        String newpassword,
        String confirmpassword
) {
}
