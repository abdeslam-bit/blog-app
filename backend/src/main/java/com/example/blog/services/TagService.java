package com.example.blog.services;

import java.util.List;
import java.util.Set;

import java.util.UUID;

import com.example.blog.domain.entities.Tag;

public interface TagService {
    List<Tag> ListTags();

    List<Tag> createTag(Set<String> names);

    void deleteTag(UUID id);
}

