package com.example.todo_app_backend.service;

import com.example.todo_app_backend.dto.CreateTaskRequest;
import com.example.todo_app_backend.dto.TaskResponse;
import com.example.todo_app_backend.dto.UpdateTaskRequest;
import com.example.todo_app_backend.entity.Task;
import com.example.todo_app_backend.entity.User;
import com.example.todo_app_backend.enums.RoleType;
import com.example.todo_app_backend.enums.TaskPriority;
import com.example.todo_app_backend.enums.TaskStatus;
import com.example.todo_app_backend.exception.TaskNotFoundException;
import com.example.todo_app_backend.exception.UserNotFoundException;
import com.example.todo_app_backend.repository.TaskRepository;
import com.example.todo_app_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(RoleType.USER);

        task = new Task();
        task.setTaskId(10L);
        task.setTitle("Test Task");
        task.setUser(user);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        task.setDueDate(LocalDate.now().plusDays(5));
    }

    @Nested
    class CreateTask {
        @Test
        void createTask_shouldCreateTaskSuccessfully() {
            CreateTaskRequest request = new CreateTaskRequest(
                    "Test Task",
                    TaskStatus.TODO,
                    TaskPriority.HIGH,
                    LocalDate.now().plusDays(5)
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            TaskResponse result = taskService.createTask(1L, request);

            assertNotNull(result);
            assertEquals(10L, result.taskId());
            assertEquals("Test Task", result.title());
            assertEquals(1L, result.userId());
            assertEquals(TaskStatus.TODO, result.status());

            verify(userRepository).findById(1L);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        void createTask_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
            CreateTaskRequest request = new CreateTaskRequest(
                    "Test Task",
                    TaskStatus.TODO,
                    TaskPriority.HIGH,
                    LocalDate.now().plusDays(5)
            );

            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> taskService.createTask(1L, request));
            verify(taskRepository, never()).save(any(Task.class));
        }
    }

    @Nested
    class GetTaskById {
        @Test
        void getTaskById_shouldReturnTask_whenTaskExists() {
            when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

            TaskResponse result = taskService.getTaskById(10L);

            assertNotNull(result);
            assertEquals(10L, result.taskId());
            assertEquals("Test Task", result.title());

            verify(taskRepository).findById(10L);
        }

        @Test
        void getTaskById_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
            when(taskRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(10L));
            verify(taskRepository).findById(10L);
        }
    }

    @Nested
    class GetTasksByUserId {
        @Test
        void getTasksByUserId_shouldReturnUserTasks() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(taskRepository.findByUserUserId(1L)).thenReturn(List.of(task));

            List<TaskResponse> result = taskService.getTasksByUserId(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Task", result.get(0).title());

            verify(userRepository).existsById(1L);
            verify(taskRepository).findByUserUserId(1L);
        }
    }

    @Nested
    class UpdateTask {
        @Test
        void updateTask_shouldUpdateTaskSuccessfully() {
            UpdateTaskRequest request = new UpdateTaskRequest(
                    "Updated Title",
                    TaskStatus.COMPLETED,
                    TaskPriority.LOW,
                    LocalDate.now().plusDays(10)
            );

            when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
            when(taskRepository.save(task)).thenReturn(task);

            TaskResponse result = taskService.updateTask(10L, request);

            assertNotNull(result);
            verify(taskRepository).findById(10L);
            verify(taskRepository).save(task);
        }
    }

    @Nested
    class DeleteTask {
        @Test
        void deleteTask_shouldDeleteTaskSuccessfully() {
            when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
            doNothing().when(taskRepository).delete(task);

            taskService.deleteTask(10L);

            verify(taskRepository).findById(10L);
            verify(taskRepository).delete(task);
        }
    }
}
