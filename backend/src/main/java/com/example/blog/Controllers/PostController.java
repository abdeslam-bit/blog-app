package com.example.blog.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.Security.BlogUserDetails;
import com.example.blog.domain.dtos.CreatePostRequest;
import com.example.blog.domain.dtos.PostDto;
import com.example.blog.domain.dtos.UpdatePostRequest;
import com.example.blog.domain.entities.Post;
import com.example.blog.mappers.PostMapper;
import com.example.blog.services.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping
    public ResponseEntity<List<PostDto>> listPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId) {
        List<Post> posts = postService.listPosts(categoryId, tagId);
        List<PostDto> postDtos = posts.stream()
                .map(postMapper::toPostDto)
                .toList();
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<PostDto>> listDrafts(
            @AuthenticationPrincipal BlogUserDetails userDetails) {
        List<Post> drafts = postService.listDrafts(userDetails.getUser());
        List<PostDto> draftDtos = drafts.stream()
                .map(postMapper::toPostDto)
                .toList();
        return ResponseEntity.ok(draftDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable UUID id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(postMapper.toPostDto(post));
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody CreatePostRequest createPostRequest,
            @AuthenticationPrincipal BlogUserDetails userDetails) {
        Post post = postService.createPost(createPostRequest, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toPostDto(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequest updatePostRequest,
            @AuthenticationPrincipal BlogUserDetails userDetails) {
        Post post = postService.updatePost(id, updatePostRequest, userDetails.getUser());
        return ResponseEntity.ok(postMapper.toPostDto(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID id,
            @AuthenticationPrincipal BlogUserDetails userDetails) {
        postService.deletePost(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}

