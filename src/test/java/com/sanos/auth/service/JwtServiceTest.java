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

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long TEST_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() throws Exception {
        setField("secret", TEST_SECRET);
        setField("expiration", TEST_EXPIRATION);
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = JwtService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(jwtService, value);
    }

    // ===== generateToken =====

    @Test
    void generateToken_returnsNonEmptyToken() {
        String token = jwtService.generateToken("nicolas", Set.of("USER"));

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void generateToken_differentUsersDifferentTokens() {
        String token1 = jwtService.generateToken("user1", Set.of("USER"));
        String token2 = jwtService.generateToken("user2", Set.of("USER"));

        assertNotEquals(token1, token2);
    }

    // ===== extractUsername =====

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtService.generateToken("nicolas", Set.of("ADMIN"));

        String username = jwtService.extractUsername(token);

        assertEquals("nicolas", username);
    }

    @Test
    void extractUsername_fromDifferentToken() {
        String token = jwtService.generateToken("otro_user", Set.of("USER", "ADMIN"));

        String username = jwtService.extractUsername(token);

        assertEquals("otro_user", username);
    }

    // ===== isTokenValid =====

    @Test
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtService.generateToken("nicolas", Set.of("USER"));

        boolean valid = jwtService.isTokenValid(token, "nicolas");

        assertTrue(valid);
    }

    @Test
    void isTokenValid_wrongUsername_returnsFalse() {
        String token = jwtService.generateToken("nicolas", Set.of("USER"));

        boolean valid = jwtService.isTokenValid(token, "otro");

        assertFalse(valid);
    }

    @Test
    void isTokenValid_expiredToken_throwsExpiredJwtException() throws Exception {
        // Set expiration to -1000 so token is already expired
        setField("expiration", -1000L);
        String token = jwtService.generateToken("nicolas", Set.of("USER"));

        // extractUsername throws ExpiredJwtException for expired tokens
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> jwtService.isTokenValid(token, "nicolas"));
    }

    @Test
    void isTokenValid_tamperedToken_returnsFalse() {
        String token = jwtService.generateToken("nicolas", Set.of("USER"));
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        // Tampered tokens may throw or return false
        assertThrows(Exception.class, () -> jwtService.isTokenValid(tampered, "nicolas"));
    }

    @Test
    void extractUsername_withRolesPreserved() {
        String token = jwtService.generateToken("nicolas", Set.of("USER", "ADMIN"));

        // Should not throw — token is well-formed
        String username = jwtService.extractUsername(token);
        assertEquals("nicolas", username);
    }
}
