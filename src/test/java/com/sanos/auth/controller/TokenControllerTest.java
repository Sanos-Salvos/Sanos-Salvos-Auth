package com.sanos.auth.controller;

import com.sanos.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private TokenController tokenController;

    @Test
    void validate_tokenValido_deberiaRetornarTrue() {
        when(authService.validateToken("good-token")).thenReturn("{\"valid\":true,\"username\":\"nicolas\"}");

        String result = tokenController.validate("good-token");

        assertTrue(result.contains("true"));
        verify(authService).validateToken("good-token");
    }

    @Test
    void validate_tokenInvalido_deberiaRetornarFalse() {
        when(authService.validateToken("bad-token")).thenReturn("{\"valid\":false,\"error\":\"Invalid token\"}");

        String result = tokenController.validate("bad-token");

        assertTrue(result.contains("false"));
    }

    @Test
    void validate_tokenVacio_deberiaLlamarService() {
        when(authService.validateToken("")).thenReturn("{\"valid\":false,\"error\":\"Invalid token\"}");

        String result = tokenController.validate("");

        assertNotNull(result);
    }

    @Test
    void refresh_tokenValido_deberiaRetornarNuevoToken() {
        when(authService.refreshToken("old-token")).thenReturn("new-token");

        String result = tokenController.refresh("Bearer old-token");

        assertEquals("new-token", result);
        verify(authService).refreshToken("old-token");
    }

    @Test
    void refresh_tokenInvalido_deberiaLanzarExcepcion() {
        when(authService.refreshToken("bad")).thenThrow(new RuntimeException("Token refresh failed"));

        assertThrows(RuntimeException.class, () -> tokenController.refresh("Bearer bad"));
    }

    @Test
    void getAuthenticatedUser_deberiaRetornarUserInfo() {
        when(authService.getUserFromToken("token")).thenReturn("{\"username\":\"nicolas\",\"roles\":[\"USER\"]}");

        String result = tokenController.getAuthenticatedUser("Bearer token");

        assertTrue(result.contains("nicolas"));
        verify(authService).getUserFromToken("token");
    }

    @Test
    void getAuthenticatedUser_tokenInvalido_deberiaRetornarError() {
        when(authService.getUserFromToken("bad")).thenReturn("{\"error\":\"User not found\"}");

        String result = tokenController.getAuthenticatedUser("Bearer bad");

        assertTrue(result.contains("error"));
    }
}
