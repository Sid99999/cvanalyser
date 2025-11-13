package com.example.hello_spring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController                // 1
@RequestMapping("/api")        // 2
public class HelloController {

    @GetMapping("/hello")     // 3
    public ResponseEntity<Map<String, String>> hello() {
        // 4
        return ResponseEntity.ok(Map.of(
                "message", "Hello Spring Boot!",
                "status", "up"
        ));
    }
}
