package com.sanos.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthApplicationTest {

    @Test
    void contextLoads() {
        // Valida que el microservicio de autenticación levante limpio con H2
    }
}