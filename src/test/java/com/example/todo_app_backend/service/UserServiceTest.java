package com.example.todo_app_backend.service;

import com.example.todo_app_backend.dto.RegistrationRequest;
import com.example.todo_app_backend.dto.UpdateUserRequest;
import com.example.todo_app_backend.dto.UpdateUserRoleRequest;
import com.example.todo_app_backend.dto.UserResponse;
import com.example.todo_app_backend.entity.User;
import com.example.todo_app_backend.enums.RoleType;
import com.example.todo_app_backend.exception.PasswordMismatchException;
import com.example.todo_app_backend.exception.UserNotFoundException;
import com.example.todo_app_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole(RoleType.USER);
    }


    @Nested
    class getAllUsers {
        @Test
        void getAllUsers_shouldReturnAllUsers() {

            User secondUser = new User();
            secondUser.setUserId(2L);
            secondUser.setName("Johny Doe");
            secondUser.setEmail("johny@example.com");
            secondUser.setRole(RoleType.USER);

            when(userRepository.findAll())
                    .thenReturn(List.of(user, secondUser));

            List<UserResponse> result = userService.getAllUsers();

            assertNotNull(result);
            assertEquals(2, result.size());

            assertEquals(1L, result.get(0).userId());
            assertEquals("John Doe", result.get(0).name());
            assertEquals("john@example.com", result.get(0).email());

            assertEquals(2L, result.get(1).userId());
            assertEquals("Johny Doe", result.get(1).name());
            assertEquals("johny@example.com", result.get(1).email());

            verify(userRepository).findAll();
        }

        @Test
        void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() {

            when(userRepository.findAll())
                    .thenReturn(List.of());

            List<UserResponse> result = userService.getAllUsers();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(userRepository).findAll();
        }
    }

    @Nested
    class GetUserById {
        @Test
        void getUserById_shouldReturnUser_whenUserExists() {

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(user));

            UserResponse result = userService.getUserById(1L);

            assertNotNull(result);
            assertEquals(1L, result.userId());
            assertEquals("John Doe", result.name());
            assertEquals("john@example.com", result.email());
            assertEquals(RoleType.USER, result.role());

            verify(userRepository).findById(1L);
        }

        @Test
        void getUserById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {

            when(userRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.getUserById(1L)
            );

            verify(userRepository).findById(1L);
        }
    }

    @Nested
    class GetUserByEmail {
        @Test
        void getUserByEmail_shouldReturnUser_whenUserExists() {

            when(userRepository.findByEmail("john@example.com"))
                    .thenReturn(Optional.of(user));

            UserResponse result =
                    userService.getUserByEmail("john@example.com");

            assertNotNull(result);
            assertEquals(1L, result.userId());
            assertEquals("john@example.com", result.email());

            verify(userRepository)
                    .findByEmail("john@example.com");
        }

        @Test
        void getUserByEmail_shouldThrowUserNotFoundException_whenUserDoesNotExist() {

            when(userRepository.findByEmail("unknown@example.com"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.getUserByEmail("unknown@example.com")
            );

            verify(userRepository)
                    .findByEmail("unknown@example.com");
        }
    }


    @Nested
    class CreateUserFromRegistrationRequest {
        @Test
        void createUserFromRegistration_shouldCreateUserSuccessfully() {

            RegistrationRequest request = new RegistrationRequest(
                    "John Doe",
                    "john@example.com",
                    "password123",
                    "password123"
            );

            when(passwordEncoder.encode("password123"))
                    .thenReturn("encodedPassword");

            when(userRepository.save(any(User.class)))
                    .thenReturn(user);

            User result = userService.createUser(request);
            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals("john@example.com", result.getEmail());
            assertEquals(RoleType.USER, result.getRole());

            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        void createUserFromRegistration_shouldThrowException_whenPasswordsDoNotMatch() {

            RegistrationRequest request = new RegistrationRequest(
                    "John Doe",
                    "john@example.com",
                    "password123",
                    "differentPassword"
            );

            assertThrows(
                    PasswordMismatchException.class,
                    () -> userService.createUser(request)
            );

            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void updateUser_shouldUpdateNameSuccessfully() {

            UpdateUserRequest request = new UpdateUserRequest("Updated Name");

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            UserResponse result = userService.updateUser(1L, request);

            assertNotNull(result);
            assertEquals("Updated Name", result.name());

            verify(userRepository).findById(1L);
            verify(userRepository).save(user);

            assertEquals("Updated Name", user.getName());
        }

        @Test
        void updateUser_shouldThrowUserNotFoundException_whenUserDoesNotExist() {

            UpdateUserRequest request = new UpdateUserRequest("Updated Name");

            when(userRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.updateUser(1L, request)
            );

            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class UpdateUserRole {
        @Test
        void updateUserRole_shouldUpdateRoleSuccessfully() {

            UpdateUserRoleRequest request = new UpdateUserRoleRequest(RoleType.ADMIN);

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            UserResponse result = userService.updateUserRole(1L, request);

            assertNotNull(result);
            assertEquals(RoleType.ADMIN, result.role());

            verify(userRepository).findById(1L);
            verify(userRepository).save(user);

            assertEquals(RoleType.ADMIN, user.getRole());
        }


        @Test
        void updateUserRole_shouldThrowUserNotFoundException_whenUserDoesNotExist() {

            UpdateUserRoleRequest request = new UpdateUserRoleRequest(RoleType.ADMIN);

            when(userRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.updateUserRole(1L, request)
            );

            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any(User.class));
        }
    }


    @Nested
    class DeleteUser {
        @Test
        void deleteUser_shouldDeleteUserSuccessfully() {

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(user));

            doNothing().when(userRepository).delete(any(User.class));

            userService.deleteUser(1L);

            verify(userRepository).findById(1L);
            verify(userRepository).delete(any(User.class));
        }

        @Test
        void deleteUser_shouldThrowUserNotFoundException_whenUserDoesNotExist() {

            when(userRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.deleteUser(1L)
            );

            verify(userRepository).findById(1L);
            verify(userRepository, never()).delete(any(User.class));
        }

    }
}