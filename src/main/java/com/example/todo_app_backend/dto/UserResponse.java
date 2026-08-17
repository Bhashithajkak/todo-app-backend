package com.example.todo_app_backend.dto;

import com.example.todo_app_backend.enums.RoleType;
import lombok.Builder;


import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long userId,
        String name,
        String email,
        RoleType role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
