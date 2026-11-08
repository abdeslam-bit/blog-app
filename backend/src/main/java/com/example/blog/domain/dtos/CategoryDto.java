package com.example.blog.domain.dtos;

import java.util.UUID;

public record CategoryDto(UUID id,
     String name,
    Long postCount) {

}