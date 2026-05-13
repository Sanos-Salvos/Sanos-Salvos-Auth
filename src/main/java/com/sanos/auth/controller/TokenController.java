package com.sanos.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanos.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    @Autowired
    private AuthService authService;

    @GetMapping("/validate")
    public String validate(@RequestParam String token) {
        return authService.validateToken(token);
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
