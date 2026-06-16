package com.sanos.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sanos.auth.model.User;
import com.sanos.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class TokenController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "username and password are required");
            return ResponseEntity.badRequest().body(errorMap);
        }

        String token = authService.authenticate(username, password);
        User user = authService.getUserByUsername(username);

        return ResponseEntity.ok(buildAuthResponse(token, username, user));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User registered = authService.register(user);
        String token = authService.generateTokenForUser(registered);

        return new ResponseEntity<>(buildAuthResponse(token, registered.getUsername(), registered), HttpStatus.CREATED);
    }

    @GetMapping("/validate-session")
    public ResponseEntity<Boolean> validateSession(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }
        String cleanToken = token.replace("Bearer ", "");
        try {
            String username = authService.validateTokenUsername(cleanToken);
            return ResponseEntity.ok(username != null);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authService.refreshToken(token));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAuthenticatedUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authService.getUserFromToken(token));
    }

    private Map<String, Object> buildAuthResponse(String token, String username, User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("tipoToken", "Bearer");
        response.put("username", username);
        response.put("rol", user != null && user.getRoles() != null
                ? user.getRoles().stream().findFirst().orElse("USER")
                : "USER");
        return response;
    }
}