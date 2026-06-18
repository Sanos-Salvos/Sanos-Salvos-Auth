package com.sanos.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;

class UserDTOTest {

    @Test
    void testUserDTOConstructor() {
        Set<String> roles = Collections.singleton("ROLE_USER");

        UserDTO dto = new UserDTO(1L, "user", "user@sanos.com", "Juan", "Perez", roles);

        assertEquals(1L, dto.getId());
        assertEquals("user", dto.getUsername());
        assertEquals("user@sanos.com", dto.getEmail());
        assertEquals("Juan", dto.getFirstName());
        assertEquals("Perez", dto.getLastName());
        assertEquals(roles, dto.getRoles());
    }
}
