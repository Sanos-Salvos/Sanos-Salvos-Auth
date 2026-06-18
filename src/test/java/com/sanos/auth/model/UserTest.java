package com.sanos.auth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        Set<String> roles = new HashSet<>(Collections.singletonList("ROLE_USER"));

        user.setId(10L);
        user.setUsername("sanosuser");
        user.setEmail("sanos@sanos.com");
        user.setPassword("hashed_password");
        user.setRoles(roles);

        assertEquals(10L, user.getId());
        assertEquals("sanosuser", user.getUsername());
        assertEquals("sanos@sanos.com", user.getEmail());
        assertEquals("hashed_password", user.getPassword());
        assertTrue(user.getRoles().contains("ROLE_USER"));
    }
}