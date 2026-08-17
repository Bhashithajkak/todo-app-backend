package com.example.todo_app_backend.dto;

import com.example.todo_app_backend.enums.RoleType;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull
        RoleType role
) {
}
