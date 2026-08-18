package com.example.todo_app_backend.security;

import com.example.todo_app_backend.repository.TaskRepository;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
public class TaskSecurity {
    private final TaskRepository taskRepository;

    public TaskSecurity(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean isTaskOwner(Long taskId, Long userId) {
        if (taskId == null || userId == null) {
            return false;
        }
        return taskRepository.findById(taskId)
                .map(task -> task.getUser() != null && userId.equals(task.getUser().getUserId()))
                .orElse(false);
    }
}
