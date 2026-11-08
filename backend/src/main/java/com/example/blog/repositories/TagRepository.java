package com.example.blog.repositories;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.example.blog.domain.entities.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    
    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.posts")
    List<Tag> findAllWithPosts();

    List<Tag> findByNameIn(Set<String> names);
}
