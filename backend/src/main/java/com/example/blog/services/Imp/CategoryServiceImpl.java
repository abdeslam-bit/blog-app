package com.example.blog.services.Imp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.blog.domain.entities.Category;
import com.example.blog.repositories.CategoryRepository;
import com.example.blog.services.CategoryService;

import jakarta.transaction.Transactional;
import jakarta.validation.OverridesAttribute;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> ListCatigories() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override 
    @Transactional 
    public Category createCategory(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())){
            throw new IllegalStateException("Category already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(UUID id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!existingCategory.getName().equalsIgnoreCase(category.getName()) &&
                categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalStateException("Category already exists");
        }

        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()){
            throw new IllegalArgumentException("Category not found");
        }
        if (optionalCategory.get().getPosts().size() > 0){
            throw new IllegalArgumentException("Category has posts");
        }
        categoryRepository.deleteById(id);
    }
    
}
