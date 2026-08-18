package com.example.todo_app_backend.dto;

import com.example.todo_app_backend.enums.TaskPriority;
import com.example.todo_app_backend.enums.TaskStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record TaskResponse(
        Long taskId,
        String title,
        Long userId,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
