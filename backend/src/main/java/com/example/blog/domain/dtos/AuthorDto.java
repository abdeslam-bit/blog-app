package com.example.blog.domain.dtos;

import java.util.UUID;

public record AuthorDto(
    UUID id,
    String name
) {
}

