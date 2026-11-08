package com.example.blog.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.blog.domain.entities.User;
import com.example.blog.repositories.UserRepository;

@Configuration
public class SecurityConfig {

    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://localhost}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Allow error dispatch
                .requestMatchers("/error").permitAll()
                // Authentication endpoints
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                // Drafts endpoint: strictly authenticated (must precede /api/v1/posts/** GET wildcard)
                .requestMatchers(HttpMethod.GET, "/api/v1/posts/drafts").authenticated()
                // Public read-only endpoints for browsing the blog
                .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/tags/**").permitAll()
                // All mutating post, category, and tag endpoints require authentication
                .requestMatchers(HttpMethod.POST, "/api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/categories/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/tags/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/tags/**").authenticated()
                // Any other endpoint must be authenticated
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();

        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean 
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CommandLineRunner initDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User defaultUser = User.builder()
                    .name("Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("password"))
                    .build();
                userRepository.save(defaultUser);
            }
        };
    }
}
