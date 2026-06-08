# Sanos-Salvos-Auth

Microservicio de autenticacion y tokens JWT

## Puerto

8088

## Base de datos

auth_db

## Endpoints disponibles

POST /api/auth/login
POST /api/auth/register
GET /api/auth/validate?token=
GET /api/auth/validate-session
POST /api/auth/refresh

## Ejecucion con Docker

docker-compose up --build

## Ejecucion manual

mvn spring-boot:run

## Tecnologias

- Java 21
- Spring Boot 3.2
- Spring Security + JWT
- PostgreSQL
- Docker
