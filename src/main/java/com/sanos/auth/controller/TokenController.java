package com.sanos.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
            Map<String, Object> error = new HashMap<>();
            error.put("error", "username and password are required");
            return ResponseEntity.badRequest().body(error);
        }

        String token = authService.authenticate(username, password);
        User user = authService.getUserByUsername(username);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("tipoToken", "Bearer");
        response.put("username", username);
        response.put("rol", user != null && user.getRoles() != null
                ? user.getRoles().stream().findFirst().orElse("USER")
                : "USER");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User registered = authService.register(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", null);
        response.put("tipoToken", null);
        response.put("username", registered.getUsername());
        response.put("rol", registered.getRoles() != null
                ? registered.getRoles().stream().findFirst().orElse("USER")
                : "USER");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public String validate(@RequestParam String token) {
        return authService.validateToken(token);
    }

    @GetMapping("/validate-session")
    public ResponseEntity<Boolean> validateSession(@RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        try {
            String username = authService.validateTokenUsername(cleanToken);
            return ResponseEntity.ok(username != null);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/refresh")
    public String refresh(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return authService.refreshToken(token);
    }

    @GetMapping("/user")
    public String getAuthenticatedUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return authService.getUserFromToken(token);
    }
}
