package com.example.blog.domain.dtos;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.example.blog.domain.PostStatus;

public record PostDto(
    UUID id,
    String title,
    String content,
    AuthorDto author,
    CategoryDto category,
    Set<TagDto> tags,
    Integer readingTime,
    LocalDate createdAt,
    LocalDate updatedAt,
    PostStatus status
) {
}

