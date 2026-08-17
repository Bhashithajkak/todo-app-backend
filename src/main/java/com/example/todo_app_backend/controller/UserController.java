package com.example.todo_app_backend.controller;

import com.example.todo_app_backend.dto.RegistrationRequest;
import com.example.todo_app_backend.dto.UpdateUserRequest;
import com.example.todo_app_backend.dto.UpdateUserRoleRequest;
import com.example.todo_app_backend.dto.UserResponse;
import com.example.todo_app_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<List<UserResponse>>getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId){
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse>createUser(@Valid @RequestBody RegistrationRequest registrationRequest){
        UserResponse userResponse = userService.createUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse userResponse = userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserResponse> changeUserRole(@PathVariable Long userId, @Valid @RequestBody UpdateUserRoleRequest updateUserRoleRequest){
        UserResponse userResponse = userService.updateUserRole(userId, updateUserRoleRequest);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser( @PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
