package com.sanos.auth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sanos.auth.model.User;
import com.sanos.auth.dto.UserDTO;
import com.sanos.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "username and password are required");
            return ResponseEntity.badRequest().body(errorMap);
        }

        boolean isAuthenticated = authService.authenticate(username, password);

        if (!isAuthenticated) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
        }

        User user = authService.getUserByUsername(username);
        return ResponseEntity.ok(buildAuthResponse(username, user));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User registered = authService.register(user);
        return new ResponseEntity<>(buildAuthResponse(registered.getUsername(), registered), HttpStatus.CREATED);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return ResponseEntity.ok(authService.updateUser(id, userDetails));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    private Map<String, Object> buildAuthResponse(String username, User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("autenticado", true);
        response.put("username", username);

        String assignedRole = "USER";
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            String fullRole = user.getRoles().stream().findFirst().orElse("ROLE_USER");
            assignedRole = fullRole.replace("ROLE_", "");
        }

        response.put("rol", assignedRole);
        return response;
    }
}