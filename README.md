# 🃏 Blackjack Casino Platform

Sistema de **casino online de Blackjack** desarrollado bajo una arquitectura de **microservicios**, siguiendo principios de **Domain-Driven Design (DDD)**, **Arquitectura Hexagonal (Ports & Adapters)** y **CQRS**.

Cada servicio es independiente, posee su propia base de datos y se comunica mediante eventos utilizando **Apache Kafka**, lo que permite una solución escalable, desacoplada y preparada para ambientes distribuidos.

---

# 🚀 Tecnologías

### Backend
- Java 21 + Spring Boot 3
- Python + FastAPI
- Node.js + Express

### Frontend
- React
- Vite
- Nginx

### Bases de datos
- MySQL
- MongoDB

### Infraestructura
- Docker
- Docker Compose
- Apache Kafka
- Zookeeper
- Netflix Eureka

### Seguridad
- JWT
- BCrypt
- MFA (TOTP)

---

# 📐 Arquitectura

El proyecto implementa una arquitectura de **microservicios**, donde cada servicio encapsula su lógica de negocio y mantiene independencia tecnológica y de almacenamiento.

```
                +----------------+
                |    Frontend    |
                | React + Vite   |
                +-------+--------+
                        |
        ---------------------------------------
        |          |         |               |
        ▼          ▼         ▼               ▼
  Auth Service Wallet Service Game Service Admin Service
        |          |         |               |
        -------------------------------
                     |
                  Kafka
                     |
        -------------------------------
                     |
             Otros consumidores

               Eureka Discovery
```

Todos los servicios se registran automáticamente en **Netflix Eureka**, permitiendo el descubrimiento dinámico entre ellos sin depender de direcciones IP fijas.

---

# 🏛 Arquitectura interna de los servicios

Cada microservicio sigue el patrón **Hexagonal Architecture (Ports & Adapters)** junto con principios de **Domain-Driven Design (DDD)**.

```
src
├── domain
│   ├── model
│   ├── port
│   │   ├── in
│   │   └── out
│   ├── exception
│
├── application
│   └── usecase
│
└── infrastructure
    ├── adapter
    │   ├── in
    │   └── out
    └── config
```

## Domain

Contiene únicamente las reglas del negocio.

- Entidades
- Objetos de valor
- Excepciones
- Interfaces (Ports)

No depende de frameworks ni tecnologías externas.

## Application

Implementa los casos de uso definidos por el dominio.

## Infrastructure

Contiene las implementaciones técnicas:

- Controladores REST
- Repositorios
- Productores y consumidores Kafka
- Configuración de Spring/FastAPI
- Seguridad
- Persistencia

---

# 📚 CQRS

El proyecto implementa **Command Query Responsibility Segregation (CQRS)** separando el modelo de escritura del modelo de lectura.

| Operación | Base de datos | Información |
|-----------|--------------|-------------|
| Write Side | MySQL | Usuarios, wallets, partidas, apuestas, transacciones |
| Read Side | MongoDB | Historial de partidas, sesiones, consultas optimizadas |

Esta separación permite optimizar el rendimiento y la escalabilidad del sistema.

---

# 📡 Comunicación entre servicios

La comunicación se realiza mediante dos mecanismos:

## Comunicación síncrona

Utilizada únicamente cuando la respuesta inmediata es indispensable.

Ejemplo:

```
Game Service
      │
      ▼
Wallet Service

¿Tiene saldo suficiente?
```

---

## Comunicación asíncrona

Para desacoplar los servicios se utilizan eventos publicados en **Apache Kafka**.

### Topics

| Topic | Descripción |
|--------|-------------|
| auth-events | Registro e inicio de sesión |
| wallet-events | Depósitos, retiros y ganancias |
| game-events | Inicio y finalización de partidas |
| admin-events | Eventos administrativos |
| audit-events | Auditoría (reservado) |

---

## Flujo de una partida

```
Jugador realiza apuesta
        │
        ▼
Game Service
        │
        │ HTTP
        ▼
Wallet Service
(verifica saldo)

        │
        ▼
Se juega la partida

        │
        ▼
GAME_FINISHED

        │
        ▼
Kafka
        │
 ┌──────┴────────┐
 ▼               ▼
Wallet       Admin
Service      Service

Acredita     Actualiza
ganancias    estadísticas
```

---

# 🔎 Descubrimiento de servicios

Todos los microservicios se registran automáticamente en **Netflix Eureka**.

Panel de administración:

```
http://localhost:8761
```

Los servicios pueden comunicarse utilizando nombres lógicos como:

```
AUTH-SERVICE
GAME-SERVICE
WALLET-SERVICE
ADMIN-SERVICE
```

---

# 🧩 Microservicios

| Servicio | Tecnología | Puerto | Responsabilidad |
|----------|------------|--------|-----------------|
| auth-service | Spring Boot 3 | 8081 | Registro, autenticación, JWT y MFA |
| wallet-service | FastAPI | 8082 | Gestión de billetera, depósitos, retiros y ganancias |
| game-service | Node.js + Express | 8083 | Lógica del Blackjack |
| admin-service | FastAPI | 8085 | Administración, usuarios y reportes |
| audit-service | Pendiente | — | Auditoría futura |

---

# 💻 Frontend

El cliente está desarrollado con:

- React
- Vite

En producción es servido mediante **Nginx** dentro de un contenedor Docker.

Cada módulo consume directamente el microservicio correspondiente:

| Funcionalidad | Servicio |
|--------------|----------|
| Login y Registro | Auth Service |
| Billetera | Wallet Service |
| Blackjack | Game Service |
| Administración | Admin Service |

La autenticación utiliza **JWT**, almacenado en `localStorage` y enviado mediante el encabezado:

```
Authorization: Bearer <token>
```

---

# 🔐 Seguridad

El sistema implementa diferentes mecanismos de seguridad:

- Contraseñas protegidas mediante **BCrypt**
- Autenticación basada en **JWT**
- Firma HMAC-SHA256
- MFA opcional mediante **TOTP**
- Configuración de **CORS** para el frontend
- Validación de credenciales en cada servicio

---

# 🐳 Infraestructura

Toda la plataforma se ejecuta utilizando **Docker Compose**.

Servicios desplegados:

- Frontend
- Auth Service
- Wallet Service
- Game Service
- Admin Service
- Audit Service
- MySQL
- MongoDB
- Kafka
- Zookeeper
- Eureka Server

Con un único comando se despliega toda la plataforma:

```bash
docker compose up --build
```

---

# 📂 Estructura del proyecto

```
blackjack-platform/

├── frontend/
│
├── services/
│   ├── auth-service/
│   ├── wallet-service/
│   ├── game-service/
│   ├── admin-service/
│   └── audit-service/
│
├── platform/
│   ├── mysql/
│   ├── mongodb/
│   ├── kafka/
│   ├── eureka/
│   └── docker-compose.yml
│
└── README.md
```

---

# 🎯 Características principales

- Arquitectura de microservicios
- Domain-Driven Design (DDD)
- Arquitectura Hexagonal
- CQRS
- Comunicación basada en eventos
- Apache Kafka
- Descubrimiento de servicios con Eureka
- Frontend desacoplado
- Seguridad con JWT y MFA
- Infraestructura completamente contenerizada con Docker

---

# 📄 Licencia

Este proyecto fue desarrollado con fines académicos y de aprendizaje de arquitecturas modernas de software y sistemas distribuidos.
