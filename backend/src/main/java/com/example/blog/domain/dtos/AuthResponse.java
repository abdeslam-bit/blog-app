package com.example.blog.domain.dtos;

public record AuthResponse(
    String token,
    Long expiresIn
) {

}
