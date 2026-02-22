package com.example.hello_spring.controller;

import com.example.hello_spring.dto.LoginRequest;
import com.example.hello_spring.dto.LoginResponse;
import com.example.hello_spring.dto.RegisterRequest;
import com.example.hello_spring.security.JwtService;
import com.example.hello_spring.service.UserService;
import com.example.hello_spring.repository.UserRepository;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        log.info("Login attempt for username '{}'", request.getUsername());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        var roles = authentication.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .toList();

        String token = jwtService.generateToken(
                request.getUsername(),
                roles
        );

        log.info("Login successful for username '{}'", request.getUsername());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }

        userService.createUser(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok("User registered successfully");
    }
}