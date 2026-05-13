# Sanos-Salvos-Auth

Servicio de validación y renovación de tokens JWT basado en Spring Boot y Kafka.

## Responsabilidad

- **Valida tokens JWT** sin requerir credenciales
- **Refresca tokens expirados**
- **Obtiene datos del usuario autenticado** desde el token

## Estructura del proyecto

- `Dockerfile`
- `pom.xml`
- `README.md`
- `src/main/java/com/sanos/auth/`
- `src/main/resources/application.properties`
- `target/` (artefactos compilados)

## Puerto

- `8088`

## Endpoints

- `GET /api/auth/validate?token={token}` - Valida un token JWT (público)
- `POST /api/auth/refresh` - Refresca un token expirado (requiere Authorization header)
- `GET /api/auth/user` - Obtiene datos del usuario del token (requiere Authorization header)

## Configuración principal

- `server.port=8088`
- `spring.kafka.bootstrap-servers=localhost:9092`
- `spring.kafka.consumer.group-id=auth-group`
- `jwt.secret=mySecretKey`
- `jwt.expiration=86400000`

## Flujo de uso

1. El BFF llama a `Login` con credenciales
2. `Login` genera un token JWT
3. El BFF llama a `Auth` para validar el token
4. Cuando el token expira, `Auth` puede refrescarlo

## Ejemplos con curl

```bash
# Validar token
curl -X GET 'http://localhost:8088/api/auth/validate?token=eyJhbGc...'

# Refrescar token
curl -X POST 'http://localhost:8088/api/auth/refresh' \
  -H 'Authorization: Bearer eyJhbGc...'

# Obtener datos del usuario
curl -X GET 'http://localhost:8088/api/auth/user' \
  -H 'Authorization: Bearer eyJhbGc...'
```

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