package com.example.todo_app_backend.service;

import com.example.todo_app_backend.dto.AuthResponse;
import com.example.todo_app_backend.dto.LoginRequest;
import com.example.todo_app_backend.dto.RegistrationRequest;
import com.example.todo_app_backend.entity.User;
import com.example.todo_app_backend.exception.TokenValidationException;
import com.example.todo_app_backend.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password())
        );
        User user = userService.getUserEntityByEmail(loginRequest.email());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse register(RegistrationRequest registrationRequest){

        User savedUser = userService.createUser(registrationRequest);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken) {

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new TokenValidationException("Provided token is not a refresh token");
        }

        Claims claims = jwtService.validateToken(refreshToken);

        String email = claims.getSubject();
        User user = userService.getUserEntityByEmail(email);

        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(newAccessToken, refreshToken);
    }
}
