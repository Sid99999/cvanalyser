package com.example.hello_spring.config;

import com.example.hello_spring.model.Role;
import com.example.hello_spring.model.User;
import com.example.hello_spring.repository.RoleRepository;
import com.example.hello_spring.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Profile("dev")
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // =========================
            // Create roles if missing
            // =========================
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

            // =========================
            // Create default user: sid
            // =========================
            if (userRepository.findByUsername("sid").isEmpty()) {
                User user = new User();
                user.setUsername("sid");
                user.setPassword(passwordEncoder.encode("yourpassword"));
                user.setEnabled(true);
                user.setRoles(Set.of(userRole));

                userRepository.save(user);
            }

            // =========================
            // Create second user: alice
            // =========================
            if (userRepository.findByUsername("alice").isEmpty()) {
                User alice = new User();
                alice.setUsername("alice");
                alice.setPassword(passwordEncoder.encode("alicepassword"));
                alice.setEnabled(true);
                alice.setRoles(Set.of(userRole));

                userRepository.save(alice);
            }

            System.out.println("✅ Default users & roles initialized");
        };
    }
}
