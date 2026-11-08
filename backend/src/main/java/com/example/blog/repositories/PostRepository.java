package com.example.blog.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.domain.PostStatus;
import com.example.blog.domain.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags WHERE p.id = :id")
    Optional<Post> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags WHERE p.status = :status ORDER BY p.createdAt DESC, p.id DESC")
    List<Post> findAllByStatus(@Param("status") PostStatus status);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags WHERE p.status = :status AND p.category.id = :categoryId ORDER BY p.createdAt DESC, p.id DESC")
    List<Post> findAllByStatusAndCategory(@Param("status") PostStatus status, @Param("categoryId") UUID categoryId);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags t WHERE p.status = :status AND t.id = :tagId ORDER BY p.createdAt DESC, p.id DESC")
    List<Post> findAllByStatusAndTag(@Param("status") PostStatus status, @Param("tagId") UUID tagId);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags t WHERE p.status = :status AND p.category.id = :categoryId AND t.id = :tagId ORDER BY p.createdAt DESC, p.id DESC")
    List<Post> findAllByStatusAndCategoryAndTag(@Param("status") PostStatus status, @Param("categoryId") UUID categoryId, @Param("tagId") UUID tagId);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags WHERE p.status = :status AND p.author.id = :authorId ORDER BY p.updatedAt DESC, p.id DESC")
    List<Post> findAllByStatusAndAuthor(@Param("status") PostStatus status, @Param("authorId") UUID authorId);

    boolean existsByTitleIgnoreCase(String title);
}
