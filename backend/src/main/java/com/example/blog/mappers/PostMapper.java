package com.example.blog.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.example.blog.domain.PostStatus;
import com.example.blog.domain.dtos.AuthorDto;
import com.example.blog.domain.dtos.CategoryDto;
import com.example.blog.domain.dtos.PostDto;
import com.example.blog.domain.dtos.TagDto;
import com.example.blog.domain.entities.Category;
import com.example.blog.domain.entities.Post;
import com.example.blog.domain.entities.Tag;
import com.example.blog.domain.entities.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "author", source = "author", qualifiedByName = "toAuthorDto")
    @Mapping(target = "category", source = "category", qualifiedByName = "toCategoryDto")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "toTagDtoSet")
    PostDto toPostDto(Post post);

    @Named("toAuthorDto")
    default AuthorDto toAuthorDto(User user) {
        if (user == null) {
            return null;
        }
        return new AuthorDto(user.getId(), user.getName());
    }

    @Named("toCategoryDto")
    default CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        Long postCount = null;
        if (Hibernate.isInitialized(category.getPosts()) && category.getPosts() != null) {
            postCount = category.getPosts().stream()
                    .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                    .count();
        }
        return new CategoryDto(category.getId(), category.getName(), postCount);
    }

    @Named("toTagDtoSet")
    default Set<TagDto> toTagDtoSet(Set<Tag> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .map(this::toTagDto)
                .collect(Collectors.toSet());
    }

    default TagDto toTagDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        Integer postCount = null;
        if (Hibernate.isInitialized(tag.getPosts()) && tag.getPosts() != null) {
            postCount = (int) tag.getPosts().stream()
                    .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                    .count();
        }
        return new TagDto(tag.getId(), tag.getName(), postCount);
    }
}

