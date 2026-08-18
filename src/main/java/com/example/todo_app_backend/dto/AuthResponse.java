package com.example.todo_app_backend.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {

}
