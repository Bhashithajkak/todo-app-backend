package com.example.todo_app_backend.repository;

import com.example.todo_app_backend.entity.Task;
import com.example.todo_app_backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserUserId(Long userId);
    List<Task> findByUserUserIdAndStatus(Long userId, TaskStatus status);
}
