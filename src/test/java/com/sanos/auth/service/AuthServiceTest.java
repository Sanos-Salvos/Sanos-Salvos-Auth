package com.sanos.auth.service;

import com.sanos.auth.model.User;
import com.sanos.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("nicolas");
        testUser.setEmail("nicolas@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Set.of("USER", "ADMIN"));
    }

    // ===== authenticate =====

    @Test
    void authenticate_success() {
        when(userRepository.findByUsername("nicolas")).thenReturn(testUser);
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("nicolas", Set.of("USER", "ADMIN"))).thenReturn("jwt-token");

        String token = authService.authenticate("nicolas", "rawPassword");

        assertEquals("jwt-token", token);
    }

    @Test
    void authenticate_userNotFound_throwsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authService.authenticate("unknown", "password"));
    }

    @Test
    void authenticate_wrongPassword_throwsException() {
        when(userRepository.findByUsername("nicolas")).thenReturn(testUser);
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.authenticate("nicolas", "wrong"));
    }

    // ===== register =====

    @Test
    void register_success() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(2L);
            return u;
        });

        User result = authService.register(newUser);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void register_encodesPassword() {
        User newUser = new User();
        newUser.setUsername("test");
        newUser.setPassword("plain123");

        when(passwordEncoder.encode("plain123")).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        authService.register(newUser);

        verify(userRepository).save(argThat(u -> u.getPassword().equals("$2a$encoded")));
    }

    // ===== validateToken =====

    @Test
    void validateToken_valid() {
        when(jwtService.extractUsername("valid-token")).thenReturn("nicolas");

        String result = authService.validateToken("valid-token");

        assertTrue(result.contains("true"));
        assertTrue(result.contains("nicolas"));
    }

    @Test
    void validateToken_invalid() {
        when(jwtService.extractUsername("bad-token")).thenThrow(new RuntimeException("Invalid token"));

        String result = authService.validateToken("bad-token");

        assertTrue(result.contains("false"));
    }

    // ===== refreshToken =====

    @Test
    void refreshToken_success() {
        when(jwtService.extractUsername("old-token")).thenReturn("nicolas");
        when(userRepository.findByUsername("nicolas")).thenReturn(testUser);
        when(jwtService.generateToken("nicolas", Set.of("USER", "ADMIN"))).thenReturn("new-token");

        String result = authService.refreshToken("old-token");

        assertEquals("new-token", result);
    }

    @Test
    void refreshToken_userNotFound_throwsException() {
        when(jwtService.extractUsername("token")).thenReturn("ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authService.refreshToken("token"));
    }

    // ===== getUserFromToken =====

    @Test
    void getUserFromToken_success() {
        when(jwtService.extractUsername("token")).thenReturn("nicolas");
        when(userRepository.findByUsername("nicolas")).thenReturn(testUser);

        String result = authService.getUserFromToken("token");

        assertTrue(result.contains("nicolas"));
    }

    @Test
    void getUserFromToken_userNotFound() {
        when(jwtService.extractUsername("token")).thenReturn("ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        String result = authService.getUserFromToken("token");

        assertTrue(result.contains("error"));
    }
}
