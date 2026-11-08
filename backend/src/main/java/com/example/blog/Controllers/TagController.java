package com.example.blog.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.domain.dtos.CreateTagsRequest;
import com.example.blog.domain.dtos.TagDto;
import com.example.blog.domain.entities.Tag;
import com.example.blog.mappers.TagMapper;
import com.example.blog.services.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<List<TagDto>> listTags() {
        List<TagDto> tags = tagService.ListTags().stream()
                .map(tagMapper::toTagDto)
                .toList();
                
        return ResponseEntity.ok(tags);
    }

    @PostMapping
    public ResponseEntity<List<TagDto>> createTag(@Valid @RequestBody CreateTagsRequest tagsRequest) {
        List<Tag> tags = tagService.createTag(tagsRequest.names());
        List<TagDto> tagDtos = tags.stream()
                .map(tagMapper::toTagDto)
                .toList();
        return ResponseEntity.ok(tagDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}