package com.example.todo_app_backend.dto;

import com.example.todo_app_backend.enums.TaskPriority;
import com.example.todo_app_backend.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateTaskRequest(
        @NotBlank(message = "Title cannot be blank")
        String title,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate
) {
}
