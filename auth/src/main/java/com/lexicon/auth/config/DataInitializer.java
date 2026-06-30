package com.lexicon.auth.config;

import com.lexicon.auth.entity.User;
import com.lexicon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with a demo user on application startup.
 * Uses the real PasswordEncoder bean so the BCrypt hash is always valid.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("suer").isEmpty()) {
            User demoUser = User.builder()
                    .username("suer")
                    .password(passwordEncoder.encode("1234"))
                    .role("ROLE_USER")
                    .build();
            userRepository.save(demoUser);
            log.info("Demo user 'suer' created successfully");
        } else {
            log.info("Demo user 'suer' already exists, skipping seed");
        }
    }
}
