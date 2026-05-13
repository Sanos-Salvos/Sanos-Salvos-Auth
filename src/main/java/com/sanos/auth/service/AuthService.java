package com.sanos.auth.service;

import java.util.Optional;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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
}