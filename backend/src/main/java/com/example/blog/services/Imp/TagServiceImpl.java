package com.example.blog.services.Imp;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.blog.domain.entities.Tag;
import com.example.blog.repositories.TagRepository;
import com.example.blog.services.TagService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public List<Tag> ListTags() {
        return tagRepository.findAllWithPosts();
    }

    @Override
    @Transactional
    public List<Tag> createTag(Set<String> names) {
        List<Tag> existingTags = tagRepository.findByNameIn(names);
        Set<String> existingNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> newTags = names.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> Tag.builder().name(name).build())
                .toList();
        return tagRepository.saveAll(newTags);
    }

    @Override
    @Transactional
    public void deleteTag(UUID id) {
        Optional<Tag> optionalTag = tagRepository.findById(id);
        if (optionalTag.isEmpty()) {
            throw new IllegalArgumentException("Tag not found");
        }
        if (optionalTag.get().getPosts() != null && optionalTag.get().getPosts().size() > 0) {
            throw new IllegalArgumentException("Tag has posts");
        }
        tagRepository.deleteById(id);
    }

}
