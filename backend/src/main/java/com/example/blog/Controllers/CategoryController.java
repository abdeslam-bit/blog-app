package com.example.blog.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.domain.dtos.CategoryDto;
import com.example.blog.domain.dtos.CreateCategoryRequest;
import com.example.blog.domain.dtos.UpdateCategoryRequest;
import com.example.blog.domain.entities.Category;
import com.example.blog.mappers.CategoryMapper;
import com.example.blog.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor 
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories() {
        List<CategoryDto> categoryDtos = categoryService.ListCatigories().stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
        return ResponseEntity.ok(categoryDtos);
    }

    @PostMapping() 
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest categoryCreate) {
        Category category = categoryMapper.toEntity(categoryCreate);
        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(categoryMapper.toCategoryDto(savedCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest categoryRequest) {
        Category category = categoryMapper.toEntity(categoryRequest);
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(categoryMapper.toCategoryDto(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}

