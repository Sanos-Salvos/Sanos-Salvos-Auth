package com.sanos.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sanos.auth.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_CuandoElUsuarioExiste_DeberiaRetornarUsuario() {
        User user = new User();
        user.setUsername("juanito");
        user.setEmail("juan@sanos.com");
        user.setPassword("password123"); // Seteado para evitar restricción not-null
        user.setRoles(new HashSet<>(Collections.singletonList("ROLE_USER")));

        userRepository.save(user);

        User found = userRepository.findByUsername("juanito");

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("juan@sanos.com");
    }

    @Test
    void findByUsername_CuandoElUsuarioNoExiste_DeberiaRetornarNull() {
        User found = userRepository.findByUsername("usuario_fantasma");
        assertThat(found).isNull();
    }
}