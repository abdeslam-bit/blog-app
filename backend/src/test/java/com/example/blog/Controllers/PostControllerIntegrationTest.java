package com.example.blog.Controllers;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.blog.Security.BlogUserDetails;
import com.example.blog.config.JwtHelper;
import com.example.blog.domain.PostStatus;
import com.example.blog.domain.entities.Category;
import com.example.blog.domain.entities.Post;
import com.example.blog.domain.entities.Tag;
import com.example.blog.domain.entities.User;
import com.example.blog.repositories.CategoryRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.TagRepository;
import com.example.blog.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtHelper jwtHelper;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private User testUser;
    private String jwtToken;
    private Category testCategory;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .build();
        testUser = userRepository.save(testUser);

        jwtToken = jwtHelper.generateToken(new BlogUserDetails(testUser));

        testCategory = Category.builder()
                .name("Technology")
                .build();
        testCategory = categoryRepository.save(testCategory);

        testTag = Tag.builder()
                .name("java")
                .build();
        testTag = tagRepository.save(testTag);
    }

    @Test
    void testCreateAndGetPost() throws Exception {
        String postPayload = """
            {
                "title": "Introduction to Spring Boot",
                "content": "<p>This is a great blog post about Spring Boot and Java.</p>",
                "categoryId": "%s",
                "tagIds": ["%s"],
                "status": "PUBLISHED"
            }
        """.formatted(testCategory.getId(), testTag.getId());

        String response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title", is("Introduction to Spring Boot")))
                .andExpect(jsonPath("$.status", is("PUBLISHED")))
                .andExpect(jsonPath("$.author.name", is("Test User")))
                .andExpect(jsonPath("$.category.name", is("Technology")))
                .andExpect(jsonPath("$.tags", hasSize(1)))
                .andExpect(jsonPath("$.readingTime", is(1)))
                .andReturn().getResponse().getContentAsString();

        String postId = objectMapper.readTree(response).get("id").asText();

        // Get post by ID (public)
        mockMvc.perform(get("/api/v1/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(postId)))
                .andExpect(jsonPath("$.title", is("Introduction to Spring Boot")));

        // List posts
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Introduction to Spring Boot")));
    }

    @Test
    void testDraftsEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/posts/drafts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDraftsEndpointWithAuth() throws Exception {
        // Create draft post
        String draftPayload = """
            {
                "title": "Work in progress draft",
                "content": "<p>Draft post content here.</p>",
                "categoryId": "%s",
                "tagIds": ["%s"],
                "status": "DRAFT"
            }
        """.formatted(testCategory.getId(), testTag.getId());

        mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(draftPayload))
                .andExpect(status().isCreated());

        // Drafts should NOT appear in public list
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Drafts should appear in /drafts
        mockMvc.perform(get("/api/v1/posts/drafts")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Work in progress draft")))
                .andExpect(jsonPath("$[0].status", is("DRAFT")));
    }

    @Test
    void testUpdatePost() throws Exception {
        Post post = Post.builder()
                .title("Initial Title")
                .content("<p>Initial content</p>")
                .status(PostStatus.PUBLISHED)
                .readingTime(1)
                .author(testUser)
                .category(testCategory)
                .build();
        post = postRepository.save(post);

        String updatePayload = """
            {
                "id": "%s",
                "title": "Updated Title",
                "content": "<p>Updated content here</p>",
                "categoryId": "%s",
                "tagIds": ["%s"],
                "status": "PUBLISHED"
            }
        """.formatted(post.getId(), testCategory.getId(), testTag.getId());

        mockMvc.perform(put("/api/v1/posts/" + post.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")));
    }

    @Test
    void testDeletePost() throws Exception {
        Post post = Post.builder()
                .title("Post To Delete")
                .content("<p>Content</p>")
                .status(PostStatus.PUBLISHED)
                .readingTime(1)
                .author(testUser)
                .category(testCategory)
                .build();
        post = postRepository.save(post);

        mockMvc.perform(delete("/api/v1/posts/" + post.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testCategoryUpdate() throws Exception {
        String updatePayload = """
            {
                "id": "%s",
                "name": "Science and Tech"
            }
        """.formatted(testCategory.getId());

        mockMvc.perform(put("/api/v1/categories/" + testCategory.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Science and Tech")));
    }
}
