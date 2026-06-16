package com.sanos.auth.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sanos.auth.model.User;
import com.sanos.auth.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public String authenticate(String username, String password) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return jwtService.generateToken(username, userOpt.get().getRoles());
        }
        throw new RuntimeException("Invalid credentials");
    }

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("The username '" + user.getUsername() + "' is already taken.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("The email '" + user.getEmail() + "' is already registered.");
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<String> defaultRoles = new HashSet<>();
            defaultRoles.add("ROLE_USER");
            user.setRoles(defaultRoles);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String generateTokenForUser(User user) {
        return jwtService.generateToken(user.getUsername(), user.getRoles());
    }

    public String validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            return "{\"valid\":true,\"username\":\"" + username + "\"}";
        } catch (Exception e) {
            return "{\"valid\":false,\"error\":\"Invalid token\"}";
        }
    }

    public String refreshToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOpt.isPresent()) {
                return jwtService.generateToken(username, userOpt.get().getRoles());
            }
            throw new RuntimeException("User not found");
        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    public String getUserFromToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return "{\"username\":\"" + user.getUsername() + "\",\"roles\":" + user.getRoles() + "}";
            }
            return "{\"error\":\"User not found\"}";
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user: " + e.getMessage());
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String validateTokenUsername(String token) {
        try {
            return jwtService.extractUsername(token);
        } catch (Exception e) {
            return null;
        }
    }
}