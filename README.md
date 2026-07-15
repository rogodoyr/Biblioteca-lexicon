# Lexicon - Sistema de Biblioteca Distribuida

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=spring-boot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2025.1.2-6DB33F?style=for-the-badge&logo=spring)
![JWT](https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=json-web-tokens)
![Gradle](https://img.shields.io/badge/Gradle-9.5.1-02303A?style=for-the-badge&logo=gradle)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka-Server-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-3.0.3-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![GlitchTip](https://img.shields.io/badge/GlitchTip-Sentry-FF6B6B?style=for-the-badge&logo=sentry&logoColor=white)

**Lexicon** es una arquitectura moderna basada en microservicios disenada para gestionar el ecosistema completo de una biblioteca. Implementa los mas altos estandares de escalabilidad, seguridad y observabilidad usando Spring Cloud, JWT, Swagger y GlitchTip.

---

## Arquitectura del Sistema

El proyecto sigue un diseno de **Microservicios Aislados** con descubrimiento de red dinamico, seguridad perimetral y observabilidad centralizada.

Ver el **[Diagrama de Arquitectura C2 y C3](./Diagramas.md)**

### Infraestructura

| Componente | Puerto | Rol |
|---|---|---|
| **Eureka** | 8761 | Service Discovery. Debe iniciarse primero. |
| **API Gateway** | 8080 | Punto de entrada unico. Enruta via Eureka y valida JWT. |
| **Auth** | 8081 | JWT login (`/api/v1/auth/login`). Emite tokens con HMAC-SHA384. |

### Microservicios de Dominio (CRUD)

| Servicio | Puerto | Entidad | Descripcion |
|---|---|---|---|
| **ms-book** | 8082 | Book | Gestion del inventario de libros |
| **ms-loan** | 8083 | Loan | Gestion de prestamos de libros |
| **ms-customer** | 8084 | Customer | Gestion de clientes/lectores |
| **ms-category** | 8086 | Category | Categorias y generos de libros |
| **ms-reservation** | 8087 | Reservation | Reservas de libros |
| **ms-notification** | 8088 | Notification | Notificaciones del sistema |
| **ms-penalty** | 8089 | Penalty | Multas y penalizaciones |
| **ms-report** | 8090 | Report | Reportes y analiticas |

### Servicios Especiales

| Servicio | Puerto | Rol |
|---|---|---|
| **bff** | 8085 | Backend For Frontend. Orquesta Book + Loan via OpenFeign. |

---

## Stack Tecnologico

### Core
- **Java 25** con toolchain Gradle
- **Spring Boot 4.1.0** framework principal
- **Spring Cloud 2025.1.2** orquestacion de microservicios

### Infraestructura y Red
- **Spring Cloud Netflix Eureka** descubrimiento de servicios
- **Spring Cloud Gateway** API Gateway con MVC mode
- **OpenFeign** declarative HTTP client (BFF)
- **Spring Boot RestClient** propagacion de Request-ID entre servicios

### Seguridad
- **Spring Security** autenticacion y autorizacion
- **JJWT 0.12.6** creacion y validacion de tokens JWT
- **BCrypt** encriptacion de contrasenas
- **Firma HMAC-SHA384** para tokens

### Persistencia
- **Spring Data JPA** ORM y repositorios
- **H2 Database** base de datos en memoria (desarrollo)
- **PostgreSQL** base de datos produccion
- **Flyway** migraciones versionadas de base de datos

### API y Documentacion
- **Springdoc OpenAPI 3.0.3** generacion automatica de documentacion
- **Swagger UI** interfaz interactiva de prueba de APIs
- **OpenAPI 3.1.0** estandar de documentacion

### Observabilidad y Monitoreo
- **Sentry SDK** integracion con GlitchTip para error tracking
- **sentry-spring-boot-4** auto-configuracion Spring Boot
- **sentry-logback** envio de logs a GlitchTip
- **SLF4J + Logback** framework de logging estandar
- **Request-ID** correlacion de requests entre microservicios
- **Spring Boot Actuator** endpoints de salud y metricas

### Pruebas
- **JUnit 5** framework de pruebas
- **Mockito** mocking de dependencias
- **JaCoCo** analisis de cobertura de codigo
- **Spring Boot Test** pruebas de integracion
- **Shell Scripts** integration testing E2E

### Utilidades
- **Lombok** reduccion de boilerplate (@Data, @Builder, etc.)
- **Gradle 9.5.1** build tool con wrapper

### DevOps
- **Docker** contenedores multi-stage
- **Docker Compose** orquestacion local de todos los servicios

---

## Microservicios - Detalle

### Auth Service (`auth`)
- **Puerto**: 8081
- **Paquete**: `com.lexicon.auth`
- **Entidad**: User (username, password, role)
- **Funcionalidad**: Login JWT con BCrypt, emision de tokens
- **Endpoint**: `POST /api/v1/auth/login`

### Book Service (`ms-book`)
- **Puerto**: 8082
- **Paquete**: `com.lexicon.book`
- **Entidad**: Book (title, author, isbn, genre, publicationYear)
- **Funcionalidad**: CRUD completo de libros
- **Seed Data**: 3 libros precargados

### Loan Service (`ms-loan`)
- **Puerto**: 8083
- **Paquete**: `com.lexicon.loan`
- **Entidad**: Loan (bookId, userId, loanDate, returnDate, status)
- **Funcionalidad**: CRUD completo de prestamos
- **Seed Data**: 1 prestamo precargado

### Customer Service (`ms-customer`)
- **Puerto**: 8084
- **Paquete**: `com.lexicon.customer`
- **Entidad**: Customer (rut, name, email, phone, status)
- **Funcionalidad**: CRUD completo de clientes
- **Seed Data**: 5 clientes precargados

### Category Service (`ms-category`)
- **Puerto**: 8086
- **Paquete**: `com.lexicon.category`
- **Entidad**: Category (name, description)
- **Funcionalidad**: CRUD de categorias/generos de libros
- **Seed Data**: 1 categoria precargada

### Reservation Service (`ms-reservation`)
- **Puerto**: 8087
- **Paquete**: `com.lexicon.reservation`
- **Entidad**: Reservation (bookId, userId, reservationDate, expiryDate, status)
- **Funcionalidad**: CRUD de reservas de libros
- **Seed Data**: 1 reserva precargada

### Notification Service (`ms-notification`)
- **Puerto**: 8088
- **Paquete**: `com.lexicon.notification`
- **Entidad**: Notification (userId, type, message, readStatus, createdAt)
- **Funcionalidad**: CRUD de notificaciones del sistema
- **Seed Data**: 1 notificacion precargada

### Penalty Service (`ms-penalty`)
- **Puerto**: 8089
- **Paquete**: `com.lexicon.penalty`
- **Entidad**: Penalty (loanId, userId, amount, reason, status, createdAt)
- **Funcionalidad**: CRUD de multas y penalizaciones
- **Seed Data**: 1 multa precargada

### Report Service (`ms-report`)
- **Puerto**: 8090
- **Paquete**: `com.lexicon.report`
- **Entidad**: Report (reportType, generatedBy, parameters, result, createdAt)
- **Funcionalidad**: CRUD de reportes y analiticas
- **Seed Data**: 1 reporte precargado

### BFF Service (`bff`)
- **Puerto**: 8085
- **Paquete**: `com.lexicon.bff`
- **Funcionalidad**: Orquesta Book + Loan via OpenFeign
- **Endpoint**: `GET /api/v1/bff/loans/{id}` devuelve prestamo con datos del libro

---

## Como Levantar el Entorno

### Opcion 1: Docker (Recomendado)

```bash
# Levantar todos los servicios
docker compose up --build

# Verificar en Eureka: http://localhost:8761
# Deben aparecer 11 servicios registrados
```

### Opcion 2: Local con Gradle

```bash
# Requiere Java 25 instalado
./start-services.sh

# Detener servicios
./stop-services.sh
```

### Verificacion Rapida

```bash
# Login para obtener token
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"suer","password":"1234"}'

# Probar un endpoint
curl http://localhost:8080/api/v1/books \
  -H "Authorization: Bearer <tu-token>"
```

---

## Swagger UI (Documentacion de API)

Cada servicio tiene Swagger UI habilitado y accesible en su puerto:

| Servicio | URL Swagger |
|----------|-------------|
| Auth | http://localhost:8081/swagger-ui/index.html |
| ms-book | http://localhost:8082/swagger-ui/index.html |
| ms-loan | http://localhost:8083/swagger-ui/index.html |
| ms-customer | http://localhost:8084/swagger-ui/index.html |
| bff | http://localhost:8085/swagger-ui/index.html |
| ms-category | http://localhost:8086/swagger-ui/index.html |
| ms-reservation | http://localhost:8087/swagger-ui/index.html |
| ms-notification | http://localhost:8088/swagger-ui/index.html |
| ms-penalty | http://localhost:8089/swagger-ui/index.html |
| ms-report | http://localhost:8090/swagger-ui/index.html |

---

## Pruebas

### Pruebas Unitarias

```bash
# Probar un servicio especifico
./gradlew :ms-book:test
./gradlew :ms-loan:test

# Probar todos los servicios
./gradlew test

# Probar con cobertura JaCoCo
./gradlew :ms-book:test jacocoTestReport
```

### Integration Testing

```bash
# Prueba E2E completa (login + JWT + endpoints protegidos)
./integration-test.sh
```

### Prueba con Postman

1. Importar `lexicon_postman_collection.json` en Postman
2. Ejecutar **"Login - Obtener Token"** (guarda token automaticamente)
3. Ejecutar cualquier endpoint CRUD

Ver **[Guia completa de Postman](./como_usar_postman.md)**

### Prueba con VS Code REST Client

Cada servicio tiene un archivo `api.http` con peticiones pre-configuradas. Abrir en VS Code y clic en **Send Request**.

---

## GlitchTip / Sentry - Observabilidad

El proyecto incluye integracion completa con GlitchTip (compatible con Sentry SDK) para error tracking y logging centralizado.

### Cada servicio incluye:

- **GlitchTipErrorReporter** - Reporte centralizado de errores
- **GlitchTipLogger** - Wrapper SLF4J + Sentry.logger()
- **SentryLifecycle** - Flush de eventos al apagar
- **RequestIdFilter** - Genera/reutiliza X-Request-Id
- **RequestIdContext** - MDC holder para correlacion

### Para activar GlitchTip:

```bash
# Configurar variable de entorno
export SENTRY_DSN="https://tu-key@tu-glitchtip-server/1"
export SENTRY_ENVIRONMENT=development

# Docker: agregar al docker-compose.yml
environment:
  SENTRY_DSN: "https://tu-key@tu-glitchtip-server/1"
```

Sin `SENTRY_DSN` configurado, la aplicacion funciona normalmente sin enviar datos a GlitchTip.

---

## Endpoints Principales

Todos los endpoints se acceden via API Gateway (`http://localhost:8080`):

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| POST | `/login` | Login y obtener token JWT |
| GET | `/api/v1/books` | Listar todos los libros |
| GET | `/api/v1/books/{id}` | Obtener libro por ID |
| POST | `/api/v1/books` | Crear libro |
| PUT | `/api/v1/books/{id}` | Actualizar libro |
| DELETE | `/api/v1/books/{id}` | Eliminar libro |
| GET | `/api/v1/loans` | Listar todos los prestamos |
| GET | `/api/v1/loans/{id}` | Obtener prestamo por ID |
| POST | `/api/v1/loans` | Crear prestamo |
| PUT | `/api/v1/loans/{id}` | Actualizar prestamo |
| DELETE | `/api/v1/loans/{id}` | Eliminar prestamo |
| GET | `/api/v1/customers` | Listar todos los clientes |
| GET | `/api/v1/customers/{id}` | Obtener cliente por ID |
| POST | `/api/v1/customers` | Crear cliente |
| PUT | `/api/v1/customers/{id}` | Actualizar cliente |
| DELETE | `/api/v1/customers/{id}` | Eliminar cliente |
| GET | `/api/v1/categories` | Listar categorias |
| GET | `/api/v1/reservations` | Listar reservas |
| GET | `/api/v1/notifications` | Listar notificaciones |
| GET | `/api/v1/penalties` | Listar multas |
| GET | `/api/v1/reports` | Listar reportes |
| GET | `/api/v1/bff/loans/{id}` | Prestamo consolidado (BFF) |

Todos los endpoints (excepto `/login`) requieren header:
```
Authorization: Bearer <tu-token>
```

---

## Puertos del Proyecto

| Servicio | Puerto | Swagger UI |
|----------|--------|------------|
| API Gateway | 8080 | - |
| Eureka | 8761 | - |
| Auth | 8081 | http://localhost:8081/swagger-ui/index.html |
| ms-book | 8082 | http://localhost:8082/swagger-ui/index.html |
| ms-loan | 8083 | http://localhost:8083/swagger-ui/index.html |
| ms-customer | 8084 | http://localhost:8084/swagger-ui/index.html |
| bff | 8085 | http://localhost:8085/swagger-ui/index.html |
| ms-category | 8086 | http://localhost:8086/swagger-ui/index.html |
| ms-reservation | 8087 | http://localhost:8087/swagger-ui/index.html |
| ms-notification | 8088 | http://localhost:8088/swagger-ui/index.html |
| ms-penalty | 8089 | http://localhost:8089/swagger-ui/index.html |
| ms-report | 8090 | http://localhost:8090/swagger-ui/index.html |

---

## Estructura del Proyecto

```
Biblioteca-lexicon-Desarrollo/
├── apigateway/          # API Gateway (8080)
├── auth/                # Servicio de autenticacion (8081)
├── bff/                 # Backend For Frontend (8085)
├── eureka/              # Service Discovery (8761)
├── ms-book/             # Gestion de libros (8082)
├── ms-loan/             # Gestion de prestamos (8083)
├── ms-customer/         # Gestion de clientes (8084)
├── ms-category/         # Categorias de libros (8086)
├── ms-reservation/      # Reservas de libros (8087)
├── ms-notification/     # Notificaciones (8088)
├── ms-penalty/          # Multas y penalizaciones (8089)
├── ms-report/           # Reportes y analiticas (8090)
├── docker-compose.yml   # Orquestacion Docker
├── Dockerfile           # Build multi-stage
├── settings.gradle      # Configuracion Gradle multi-modulo
├── start-services.sh    # Iniciar servicios localmente
├── stop-services.sh     # Detener servicios
├── integration-test.sh  # Prueba E2E
├── lexicon_postman_collection.json  # Coleccion Postman (43 requests)
├── Ejecutar_Proyecto.md             # Guia de ejecucion
├── como_usar_postman.md             # Guia de Postman
└── Diagramas.md                     # Diagramas C2 y C3
                      
```

### Cada microservicio sigue la estructura:

```
ms-<nombre>/
├── build.gradle
├── api.http                           # VS Code REST Client
└── src/
    ├── main/
    │   ├── java/com/lexicon/<pkg>/
    │   │   ├── <Name>Application.java
    │   │   ├── config/OpenApiConfig.java
    │   │   ├── controller/<Name>Controller.java
    │   │   ├── service/<Name>Service.java
    │   │   ├── repository/<Name>Repository.java
    │   │   ├── entity/<Name>.java
    │   │   ├── dto/<Name>RequestDto.java
    │   │   ├── dto/<Name>ResponseDto.java
    │   │   ├── exception/             # 7 clases de excepcion
    │   │   ├── glitchtip/             # 3 clases GlitchTip
    │   │   └── tracing/               # 6 clases Request-ID
    │   └── resources/
    │       ├── application.properties
    │       └── db/migration/
    │           ├── V1__init_<name>.sql
    │           └── V2__populate_data.sql
    └── test/
        ├── java/com/lexicon/<pkg>/
        │   ├── service/<Name>ServiceTest.java
        │   ├── controller/<Name>ControllerTest.java
        │   └── exception/GlobalExceptionHandlerTest.java
        └── resources/application.properties
```

---

## Convenciones del Proyecto

- **Patron de paquetes**: `com.lexicon.<modulo>` (ej: `com.lexicon.loan`)
- **Arquitectura por capas**: controller -> service -> repository -> entity
- **DTOs**: RequestDto para entrada, ResponseDto para salida
- **Excepciones**: GlobalExceptionHandler con GlitchTipErrorReporter
- **Flyway**: `V1__init` para schema, `V2__populate` para datos seed
- **Tests**: JUnit 5 + Mockito, `server.port=0` para puertos aleatorios
- **JWT**: Secreto compartido entre auth y apigateway
- **Lombok**: @Data, @Builder, @RequiredArgsConstructor en todas partes

---

## Credenciales de Prueba

| Usuario | Contrasena | Rol |
|---------|------------|-----|
| suer | 1234 | ROLE_USER |
| admin | password | ROLE_ADMIN |

---

## Guias Adicionales

- **[Ejecutar el Proyecto](./Ejecutar_Proyecto.md)** - Guia completa de ejecucion
- **[Como Usar Postman](./como_usar_postman.md)** - Guia de uso de la coleccion Postman
- **[Diagramas C2 y C3](./Diagramas.md)** - Diagramas de contenedores y componentes
