package com.example.todo_app_backend.service;

import com.example.todo_app_backend.dto.CreateTaskRequest;
import com.example.todo_app_backend.dto.TaskResponse;
import com.example.todo_app_backend.dto.UpdateTaskRequest;
import com.example.todo_app_backend.entity.Task;
import com.example.todo_app_backend.entity.User;
import com.example.todo_app_backend.enums.TaskPriority;
import com.example.todo_app_backend.enums.TaskStatus;
import com.example.todo_app_backend.exception.TaskNotFoundException;
import com.example.todo_app_backend.exception.UserNotFoundException;
import com.example.todo_app_backend.repository.TaskRepository;
import com.example.todo_app_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(Long userId, CreateTaskRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found with id:" + userId)
        );

        Task task = new Task();
        task.setTitle(request.title());
        task.setUser(user);
        task.setStatus(request.status() != null ? request.status() : TaskStatus.TODO);
        task.setPriority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM);
        task.setDueDate(request.dueDate());

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(this::mapToTaskResponse).toList();
    }

    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("Task not found with id: " + taskId)
        );
        return mapToTaskResponse(task);
    }

    public Task getTaskEntityById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("Task not found with id: " + taskId)
        );
    }

    public List<TaskResponse> getTasksByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id:" + userId);
        }
        List<Task> tasks = taskRepository.findByUserUserId(userId);
        return tasks.stream().map(this::mapToTaskResponse).toList();
    }

    public List<TaskResponse> getTasksByUserIdAndStatus(Long userId, TaskStatus status) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id:" + userId);
        }
        List<Task> tasks = taskRepository.findByUserUserIdAndStatus(userId, status);
        return tasks.stream().map(this::mapToTaskResponse).toList();
    }

    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("Task not found with id: " + taskId)
        );

        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("Task not found with id: " + taskId)
        );
        taskRepository.delete(task);
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .userId(task.getUser() != null ? task.getUser().getUserId() : null)
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
