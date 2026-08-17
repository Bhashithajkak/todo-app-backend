package com.example.todo_app_backend.service;

import com.example.todo_app_backend.dto.RegistrationRequest;
import com.example.todo_app_backend.dto.UpdateUserRequest;
import com.example.todo_app_backend.dto.UpdateUserRoleRequest;
import com.example.todo_app_backend.dto.UserResponse;
import com.example.todo_app_backend.entity.User;
import com.example.todo_app_backend.enums.RoleType;
import com.example.todo_app_backend.exception.DuplicateEmailException;
import com.example.todo_app_backend.exception.PasswordMismatchException;
import com.example.todo_app_backend.exception.UserNotFoundException;
import com.example.todo_app_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user){
        boolean isEmailExist = userRepository.existsByEmail(user.getEmail());
        if(isEmailExist){
            throw new DuplicateEmailException("Already a user with email:"+user.getEmail());
        }
        return userRepository.save(user);
    }

    public List<UserResponse> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserResponse).toList();
    }

    public UserResponse getUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new UserNotFoundException("User not found with id:"+ userId)
        );
        return mapToUserResponse(user);
    }

    public UserResponse getUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new UserNotFoundException("User not found with email:"+ email)
        );
        return mapToUserResponse(user);
    }

    public UserResponse createUser(RegistrationRequest registrationRequest){
        boolean isEmailExist = userRepository.existsByEmail(registrationRequest.email());

        if(isEmailExist){
            throw new DuplicateEmailException("Already a user with email:" + registrationRequest.email()
            );
        }
        if(!registrationRequest.password().equals(registrationRequest.confirmPassword()) ){
            throw new PasswordMismatchException("Password does not match");
        }
        User user = new User();
        user.setName(registrationRequest.name());
        user.setEmail(registrationRequest.email());
        user.setRole(RoleType.USER);
        user.setPassword(passwordEncoder.encode(registrationRequest.password()));
        return mapToUserResponse(userRepository.save(user));
    }

    //  Update user details except email and role
    public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest){

        User user = userRepository.findById(userId).orElseThrow(
                ()-> new UserNotFoundException("User not found with id:"+ userId)
        );

        user.setName(updateUserRequest.name());

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);

    }

    // Update user role has permission for admin only
    public UserResponse updateUserRole(Long userId,UpdateUserRoleRequest request){

        User user = userRepository.findById(userId).orElseThrow(
                ()-> new UserNotFoundException("User not found with id:"+ userId)
        );

        user.setRole(request.role());

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }


    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId)
                );
        userRepository.delete(user);
    }
    private UserResponse mapToUserResponse(User user){
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
