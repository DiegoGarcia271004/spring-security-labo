package com.server.app.dto.response;

import java.util.List;

public record RoleResponse(Integer id, String name, List<PermissionResponse> permissions) {
}
