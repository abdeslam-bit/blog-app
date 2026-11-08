package com.example.blog.services.Imp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.domain.PostStatus;
import com.example.blog.domain.dtos.CreatePostRequest;
import com.example.blog.domain.dtos.UpdatePostRequest;
import com.example.blog.domain.entities.Category;
import com.example.blog.domain.entities.Post;
import com.example.blog.domain.entities.Tag;
import com.example.blog.domain.entities.User;
import com.example.blog.repositories.CategoryRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.TagRepository;
import com.example.blog.repositories.UserRepository;
import com.example.blog.services.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Post> listPosts(UUID categoryId, UUID tagId) {
        if (categoryId != null && tagId != null) {
            return postRepository.findAllByStatusAndCategoryAndTag(PostStatus.PUBLISHED, categoryId, tagId);
        } else if (categoryId != null) {
            return postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED, categoryId);
        } else if (tagId != null) {
            return postRepository.findAllByStatusAndTag(PostStatus.PUBLISHED, tagId);
        } else {
            return postRepository.findAllByStatus(PostStatus.PUBLISHED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> listDrafts(User user) {
        return postRepository.findAllByStatusAndAuthor(PostStatus.DRAFT, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPost(UUID id) {
        return postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }

    @Override
    @Transactional
    public Post createPost(CreatePostRequest request, User author) {
        String trimmedTitle = request.title().trim();
        if (postRepository.existsByTitleIgnoreCase(trimmedTitle)) {
            throw new IllegalStateException("Post with this title already exists");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.categoryId()));

        Set<Tag> tags = new HashSet<>();
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            tags.addAll(tagRepository.findAllById(request.tagIds()));
        }

        User attachedAuthor = userRepository.findById(author.getId()).orElse(author);

        Post post = Post.builder()
                .title(trimmedTitle)
                .content(request.content())
                .status(request.status())
                .readingTime(calculateReadingTime(request.content()))
                .author(attachedAuthor)
                .category(category)
                .tags(tags)
                .build();

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(UUID id, UpdatePostRequest request, User currentUser) {
        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not authorized to update this post");
        }

        String trimmedTitle = request.title().trim();
        if (!post.getTitle().equalsIgnoreCase(trimmedTitle) &&
                postRepository.existsByTitleIgnoreCase(trimmedTitle)) {
            throw new IllegalStateException("Post with this title already exists");
        }

        if (!post.getCategory().getId().equals(request.categoryId())) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.categoryId()));
            post.setCategory(category);
        }

        Set<Tag> tags = new HashSet<>();
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            tags.addAll(tagRepository.findAllById(request.tagIds()));
        }
        post.setTags(tags);

        post.setTitle(trimmedTitle);
        post.setContent(request.content());
        post.setStatus(request.status());
        post.setReadingTime(calculateReadingTime(request.content()));

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(UUID id, User currentUser) {
        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not authorized to delete this post");
        }

        post.getTags().clear();
        postRepository.delete(post);
    }

    private int calculateReadingTime(String content) {
        if (content == null || content.isBlank()) {
            return 1;
        }
        String plainText = content.replaceAll("<[^>]*>", " ").trim();
        if (plainText.isBlank()) {
            return 1;
        }
        String[] words = plainText.split("\\s+");
        return Math.max(1, (int) Math.ceil((double) words.length / 200.0));
    }
}

