package com.example.hello_spring.controller;

import com.example.hello_spring.dto.LoginRequest;
import com.example.hello_spring.dto.LoginResponse;
import com.example.hello_spring.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        // 1️⃣ Authenticate username & password
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        // 2️⃣ Extract roles
        var authorities = authentication.getAuthorities();
        var roles = authorities.stream()
                .map(auth -> auth.getAuthority())
                .toList();

        // 3️⃣ Generate JWT
        String token = jwtService.generateToken(
                request.getUsername(),
                roles
        );

        // 4️⃣ Return token
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
