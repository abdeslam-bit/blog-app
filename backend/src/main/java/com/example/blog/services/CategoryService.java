package com.example.blog.services;

import java.util.List;
import java.util.UUID;

import com.example.blog.domain.entities.Category;

public interface CategoryService {
    List<Category> ListCatigories();

    Category createCategory(Category category);

    Category updateCategory(UUID id, Category category);

    void deleteCategory(UUID id);
}
