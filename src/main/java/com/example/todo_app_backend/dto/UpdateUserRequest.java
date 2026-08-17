package com.example.todo_app_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "Name is required")
        String name

) {
}
