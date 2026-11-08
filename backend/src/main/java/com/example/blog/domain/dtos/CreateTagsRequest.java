package com.example.blog.domain.dtos;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTagsRequest(
    @NotEmpty(message = "At least one tag name is required")
    @Size(max = 10, message = "Maximum {max} tags allowed")
    Set<
            @Size(min = 2, max = 30, message = "Tag name must be between {min} and {max} characters")
            @Pattern(regexp = "^[\\w\\s-]+$", message = "Tag name can only contain letters, numbers, spaces, and hyphens")
            String> names
) {

}
