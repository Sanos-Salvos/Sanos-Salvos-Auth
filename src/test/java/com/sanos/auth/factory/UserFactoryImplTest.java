package com.sanos.auth.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sanos.auth.dto.UserDTO;
import com.sanos.auth.model.User;

class UserFactoryImplTest {

    private UserFactoryImpl userFactory;

    @BeforeEach
    void setUp() {
        userFactory = new UserFactoryImpl();
    }

    @Test
    void toDTO_DeberiaMapearCamposCorrectamente() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testfactory");
        user.setEmail("factory@sanos.com");
        user.setRoles(new HashSet<>(Collections.singletonList("ROLE_USER")));

        UserDTO dto = userFactory.toDTO(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getEmail(), dto.getEmail());
    }
}