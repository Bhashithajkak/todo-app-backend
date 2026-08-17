package com.example.todo_app_backend.exception;

public class TokenGenerationException extends RuntimeException{
    public TokenGenerationException(String message, Throwable cause){
        super(message, cause);
    }
}
