package com.example.todo_app_backend.dto;

import com.example.todo_app_backend.enums.TaskPriority;
import com.example.todo_app_backend.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        String title,
        @NotNull(message = "Status is required")
        TaskStatus status,
        @NotNull(message = "Priority is required")
        TaskPriority priority,
        LocalDate dueDate
) {
}
