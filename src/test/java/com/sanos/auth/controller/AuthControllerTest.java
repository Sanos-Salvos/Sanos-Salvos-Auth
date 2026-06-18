package com.sanos.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanos.auth.dto.UserDTO;
import com.sanos.auth.model.User;
import com.sanos.auth.service.AuthService;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // <-- CRUCIAL: Esto apaga los filtros perimetrales y quita los errores 403
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private User sampleUser;
    private UserDTO sampleUserDTO;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@sanos.com");
        sampleUser.setRoles(new HashSet<>(Collections.singletonList("ROLE_USER")));

        sampleUserDTO = new UserDTO(1L, "testuser", "test@sanos.com", "Juan", "Perez", Collections.singleton("ROLE_USER"));
    }

    @Test
    void login_CredencialesValidas_DeberiaRetornarOK() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password123");

        when(authService.authenticate("testuser", "password123")).thenReturn(true);
        when(authService.getUserByUsername("testuser")).thenReturn(sampleUser);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_DeberiaRetornarLista() throws Exception {
        when(authService.getAllUsers()).thenReturn(Collections.singletonList(sampleUserDTO));

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_DeberiaRetornarUsuario() throws Exception {
        when(authService.getUserById(1L)).thenReturn(sampleUserDTO);

        mockMvc.perform(get("/api/auth/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}