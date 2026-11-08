package com.example.blog.services;

import com.example.blog.domain.dtos.AuthResponse;
import com.example.blog.domain.dtos.LoginRequest;

public interface AuthService {
    AuthResponse authenticate(String email, String password);
}
