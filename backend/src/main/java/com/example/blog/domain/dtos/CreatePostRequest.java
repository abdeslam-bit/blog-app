package com.example.blog.domain.dtos;

import java.util.Set;
import java.util.UUID;

import com.example.blog.domain.PostStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @NotBlank(message = "Content is required")
    String content,

    @NotNull(message = "Category ID is required")
    UUID categoryId,

    Set<UUID> tagIds,

    @NotNull(message = "Status is required")
    PostStatus status
) {
}

