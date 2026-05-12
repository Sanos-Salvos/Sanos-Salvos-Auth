# Sanos-Salvos-Auth

Servicio de autenticación basado en Spring Boot, JWT y Kafka.

## Puerto

- `8088`

## Endpoints

- `POST /api/auth/login?username={username}&password={password}`
- `POST /api/auth/register`

## Configuración principal

- `server.port=8088`
- `spring.kafka.bootstrap-servers=localhost:9092`
- `spring.kafka.consumer.group-id=auth-group`
- `jwt.secret=mySecretKey`
- `jwt.expiration=86400000`

## Requisitos

- Java 17
- Maven
- Kafka en `localhost:9092`

## Ejecución

```bash
cd Sanos-Salvos-Auth
mvn clean package
mvn spring-boot:run
```

O usa `docker-compose up auth-service` desde la raíz del repositorio si deseas levantarlo con el resto de los servicios.