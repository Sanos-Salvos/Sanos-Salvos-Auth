package com.sanos.auth.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEventListener {

    private static final Logger log = LoggerFactory.getLogger(UsuarioEventListener.class);

    @KafkaListener(topics = "usuarios-topic", groupId = "auth-group")
    public void escucharEventosUsuario(String message) {
        log.info("Evento asíncrono recibido en el dominio de Usuarios: {}", message);

        try {
        } catch (Exception e) {
            log.error("Error al procesar el evento de usuario en Auth: {}", e.getMessage());
        }
    }
}