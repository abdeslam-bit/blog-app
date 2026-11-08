package com.example.blog.services.Imp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.blog.Security.BlogUserDetails;
import com.example.blog.config.JwtHelper;
import com.example.blog.domain.dtos.AuthResponse;
import com.example.blog.services.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtHelper jwtHelper;

    @Override
    public AuthResponse authenticate(String email, String password) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Map<String, Object> extraClaims = new HashMap<>();
        if (userDetails instanceof BlogUserDetails blogUserDetails) {
            extraClaims.put("id", blogUserDetails.getUser().getId().toString());
            extraClaims.put("name", blogUserDetails.getUser().getName());
        }
        String token = jwtHelper.generateToken(extraClaims, userDetails);

        return new AuthResponse(token, jwtHelper.getExpiration());
    }

}
