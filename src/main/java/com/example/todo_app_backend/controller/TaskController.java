package com.example.todo_app_backend.controller;

import com.example.todo_app_backend.dto.CreateTaskRequest;
import com.example.todo_app_backend.dto.TaskResponse;
import com.example.todo_app_backend.dto.UpdateTaskRequest;
import com.example.todo_app_backend.enums.TaskStatus;
import com.example.todo_app_backend.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Create task for user
    @PreAuthorize("hasRole('ADMIN') or (isAuthenticated() and #userId == authentication.principal.userId)")
    @PostMapping("/user/{userId}")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long userId, @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ADMIN only - get all tasks
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // Get tasks by userId (optionally filtered by status)
    @PreAuthorize("hasRole('ADMIN') or (isAuthenticated() and #userId == authentication.principal.userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponse>> getTasksByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) TaskStatus status
    ) {
        List<TaskResponse> tasks;
        if (status != null) {
            tasks = taskService.getTasksByUserIdAndStatus(userId, status);
        } else {
            tasks = taskService.getTasksByUserId(userId);
        }
        return ResponseEntity.ok(tasks);
    }

    // Get task by ID
    @PreAuthorize("hasRole('ADMIN') or (isAuthenticated() and @taskSecurity.isTaskOwner(#taskId, authentication.principal.userId))")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        TaskResponse task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    // Update task
    @PreAuthorize("hasRole('ADMIN') or (isAuthenticated() and @taskSecurity.isTaskOwner(#taskId, authentication.principal.userId))")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(response);
    }

    // Delete task
    @PreAuthorize("hasRole('ADMIN') or (isAuthenticated() and @taskSecurity.isTaskOwner(#taskId, authentication.principal.userId))")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
