package com.example.todo_app_backend.controller;

import com.example.todo_app_backend.dto.UpdateUserRequest;
import com.example.todo_app_backend.dto.UpdateUserRoleRequest;
import com.example.todo_app_backend.dto.UserResponse;
import com.example.todo_app_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>>getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId){
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    // Authenticated users
    @PreAuthorize("hasRole('ADMIN') or (isAuthenticated() and #userId == authentication.principal.userId)")
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse userResponse = userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(userResponse);
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserResponse> changeUserRole(@PathVariable Long userId, @Valid @RequestBody UpdateUserRoleRequest updateUserRoleRequest){
        UserResponse userResponse = userService.updateUserRole(userId, updateUserRoleRequest);
        return ResponseEntity.ok(userResponse);
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser( @PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
