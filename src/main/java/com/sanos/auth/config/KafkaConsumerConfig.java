package com.sanos.auth.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerConfig {

    @KafkaListener(topics = "pet-topic", groupId = "auth-group")
    public void escucharMascotas(String message) {
        System.out.println("Evento recibido desde PET (auth): " + message);
    }
}
