package com.example.blog.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.example.blog.domain.PostStatus;
import com.example.blog.domain.dtos.CategoryDto;
import com.example.blog.domain.dtos.CreateCategoryRequest;
import com.example.blog.domain.dtos.UpdateCategoryRequest;
import com.example.blog.domain.entities.Category;
import com.example.blog.domain.entities.Post;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "postCount", source = "posts", qualifiedByName = "countPosts")
    CategoryDto toCategoryDto(Category category);

    Category toEntity(CreateCategoryRequest createCategoryRequest);

    Category toEntity(UpdateCategoryRequest updateCategoryRequest);
    
    @Named("countPosts")
    default Long countPosts(List<Post> posts) {
        if (posts == null){
            return 0L;
        }
        return (long) posts.stream()
               .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
               .count();
    }   
}

