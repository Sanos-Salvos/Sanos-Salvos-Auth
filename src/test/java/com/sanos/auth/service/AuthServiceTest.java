package com.sanos.auth.service;

import com.sanos.auth.model.User;
import com.sanos.auth.dto.UserDTO;
import com.sanos.auth.factory.IUserFactory;
import com.sanos.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IUserFactory userFactory;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@sanosysalvos.com");
        sampleUser.setPassword("encodedPassword");
        sampleUser.setFirstName("Juan");
        sampleUser.setLastName("Perez");
        sampleUser.setRoles(new HashSet<>(Collections.singletonList("ROLE_USER")));
    }

    @Test
    void authenticate_CredencialesCorrectas_DeberiaRetornarTrue() {
        when(userRepository.findByUsername("testuser")).thenReturn(sampleUser);
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        boolean result = authService.authenticate("testuser", "rawPassword");
        assertTrue(result);
    }

    @Test
    void authenticate_CredencialesIncorrectas_DeberiaRetornarFalse() {
        when(userRepository.findByUsername("testuser")).thenReturn(sampleUser);
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        boolean result = authService.authenticate("testuser", "wrong");
        assertFalse(result);
    }

    @Test
    void register_NombreUsuarioDuplicado_DeberiaLanzarIllegalArgumentException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.register(sampleUser));
    }

    @Test
    void register_EmailDuplicado_DeberiaLanzarIllegalArgumentException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@sanosysalvos.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.register(sampleUser));
    }

    @Test
    void register_UsuarioNuevoSinRoles_DeberiaAsignarRoleUserPorDefecto() {
        User nuevoUser = new User();
        nuevoUser.setUsername("newuser");
        nuevoUser.setPassword("raw");
        nuevoUser.setRoles(null);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User guardado = authService.register(nuevoUser);
        assertTrue(guardado.getRoles().contains("ROLE_USER"));
    }

    @Test
    void register_UsuarioConRolOrganizacion_DeberiaNormalizarARoleOrganizacion() {
        User nuevoUser = new User();
        nuevoUser.setUsername("orguser");
        nuevoUser.setPassword("raw");
        nuevoUser.setRoles(new HashSet<>(Collections.singletonList("ORGANIZACION")));

        when(userRepository.existsByUsername("orguser")).thenReturn(false);
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User guardado = authService.register(nuevoUser);
        assertTrue(guardado.getRoles().contains("ROLE_ORGANIZACION"));
    }

    @Test
    void getUserByUsername_DeberiaRetornarUsuario() {
        when(userRepository.findByUsername("testuser")).thenReturn(sampleUser);
        User result = authService.getUserByUsername("testuser");
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getAllUsers_DeberiaRetornarListaDeDTOs() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(sampleUser));
        when(userFactory.toDTO(any(User.class))).thenReturn(new UserDTO());

        List<UserDTO> result = authService.getAllUsers();
        assertFalse(result.isEmpty());
        verify(userFactory, times(1)).toDTO(any(User.class));
    }

    @Test
    void getUserById_Existente_DeberiaRetornarDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userFactory.toDTO(sampleUser)).thenReturn(new UserDTO());

        UserDTO dto = authService.getUserById(1L);
        assertNotNull(dto);
    }

    @Test
    void getUserById_NoExistente_DeberiaLanzarRuntimeException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.getUserById(99L));
    }

    @Test
    void updateUser_Existente_DeberiaModificarCampos() {
        User detallesNuevos = new User();
        detallesNuevos.setUsername("updatedname");
        detallesNuevos.setEmail("new@email.com");
        detallesNuevos.setPassword("newpass");
        detallesNuevos.setRoles(new HashSet<>(Collections.singletonList("ORGANIZACION")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("newpass")).thenReturn("newencoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userFactory.toDTO(any(User.class))).thenReturn(new UserDTO());

        UserDTO result = authService.updateUser(1L, detallesNuevos);
        assertNotNull(result);
    }

    @Test
    void deleteUser_Existente_DeberiaEliminar() {
        when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> authService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NoExistente_DeberiaLanzarRuntimeException() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.deleteUser(99L));
    }
}