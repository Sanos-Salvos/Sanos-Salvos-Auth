package com.sanos.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanos.auth.dto.UserDTO;
import com.sanos.auth.model.User;
import com.sanos.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_CamposVacios_DeberiaRetornarBadRequest() throws Exception {
        Map<String, String> bodyIncompleto = new HashMap<>();
        bodyIncompleto.put("username", "admin");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyIncompleto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_Exitoso_DeberiaRetornarOKConRol() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "user");
        body.put("password", "correct");

        User mockUser = new User();
        mockUser.setUsername("user");
        mockUser.setRoles(new HashSet<>(Collections.singletonList("ROLE_USER")));

        when(authService.authenticate("user", "correct")).thenReturn(true);
        when(authService.getUserByUsername("user")).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(true))
                .andExpect(jsonPath("$.rol").value("USER"));
    }

    @Test
    void register_Exitoso_DeberiaRetornarCreated() throws Exception {
        User user = new User();
        user.setUsername("registerUser");
        user.setPassword("password");

        when(authService.register(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("registerUser"));
    }

    @Test
    void getAllUsers_DeberiaRetornarLista() throws Exception {
        when(authService.getAllUsers()).thenReturn(Collections.singletonList(new UserDTO()));

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUserById_DeberiaRetornarUsuario() throws Exception {
        UserDTO dto = new UserDTO();
        when(authService.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/auth/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_DeberiaRetornarUsuarioModificado() throws Exception {
        User userDetails = new User();
        userDetails.setUsername("modificado");

        when(authService.updateUser(eq(1L), any(User.class))).thenReturn(new UserDTO());

        mockMvc.perform(put("/api/auth/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_DeberiaRetornarOk() throws Exception {
        org.mockito.Mockito.doNothing().when(authService).deleteUser(1L);

        mockMvc.perform(delete("/api/auth/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado correctamente"));
    }
}