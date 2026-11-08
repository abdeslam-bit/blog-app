package com.example.blog.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
        String name) {
}
