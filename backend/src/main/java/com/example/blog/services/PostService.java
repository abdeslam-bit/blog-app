package com.example.blog.services;

import java.util.List;
import java.util.UUID;

import com.example.blog.domain.dtos.CreatePostRequest;
import com.example.blog.domain.dtos.UpdatePostRequest;
import com.example.blog.domain.entities.Post;
import com.example.blog.domain.entities.User;

public interface PostService {

    List<Post> listPosts(UUID categoryId, UUID tagId);

    List<Post> listDrafts(User user);

    Post getPost(UUID id);

    Post createPost(CreatePostRequest createPostRequest, User author);

    Post updatePost(UUID id, UpdatePostRequest updatePostRequest, User currentUser);

    void deletePost(UUID id, User currentUser);
}

