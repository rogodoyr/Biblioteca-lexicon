# Informe Técnico: Sistema de Biblioteca Distribuida Lexicon

**Asignatura:** [Completar]  
**Estudiante:** [Completar]  
**Docente:** [Completar]  
**Fecha:** [Completar]

## 1. Introducción

Lexicon es un sistema de gestión de biblioteca desarrollado mediante una arquitectura de microservicios. La solución separa las funciones del negocio en servicios independientes, permitiendo administrar libros, clientes, préstamos, categorías, reservas, notificaciones, multas y reportes.

El proyecto incorpora descubrimiento de servicios, autenticación JWT, documentación OpenAPI, pruebas con Postman y despliegue con Docker Compose. De esta manera se obtiene una solución distribuida que puede ser ejecutada y validada de forma local.

## 2. Objetivo general

Diseñar e implementar un sistema distribuido para la gestión de una biblioteca, basado en microservicios independientes, seguros y desplegables mediante contenedores Docker.

## 3. Objetivos específicos

- Implementar operaciones CRUD para las entidades principales de una biblioteca.
- Centralizar el acceso mediante un API Gateway.
- Proteger los recursos con autenticación basada en JSON Web Token (JWT).
- Registrar y descubrir servicios con Eureka Server.
- Documentar y validar las APIs con Swagger, Postman y archivos api.http.
- Orquestar el entorno completo mediante Docker Compose.
- Incorporar trazabilidad de solicitudes y soporte de reporte de errores con GlitchTip/Sentry.

## 4. Arquitectura de la solución

El sistema sigue una arquitectura de microservicios. Cada servicio se encarga de un área específica del negocio y se registra en Eureka. El cliente consume las APIs a través del API Gateway, que valida el token JWT y enruta la solicitud al servicio de destino.

El flujo principal consiste en: el usuario inicia sesión; el servicio Auth entrega un token JWT; el cliente adjunta el token en el encabezado Authorization; el Gateway valida el token y deriva la petición al microservicio correspondiente; finalmente, el servicio procesa la operación y responde al cliente.

### Componentes de infraestructura

| Componente | Puerto | Responsabilidad |
|---|---:|---|
| Eureka Server | 8761 | Registro y descubrimiento de microservicios. |
| API Gateway | 8080 | Punto de acceso único, enrutamiento y validación de JWT. |
| Auth | 8081 | Inicio de sesión y emisión de tokens JWT. |
| BFF | 8085 | Composición de información de préstamos y libros. |

### Microservicios de dominio

| Microservicio | Puerto | Entidad | Función |
|---|---:|---|---|
| ms-book | 8082 | Book | Gestión del catálogo de libros. |
| ms-loan | 8083 | Loan | Gestión de préstamos. |
| ms-customer | 8084 | Customer | Gestión de clientes. |
| ms-category | 8086 | Category | Gestión de categorías y géneros. |
| ms-reservation | 8087 | Reservation | Gestión de reservas. |
| ms-notification | 8088 | Notification | Gestión de notificaciones. |
| ms-penalty | 8089 | Penalty | Gestión de multas. |
| ms-report | 8090 | Report | Gestión de reportes. |

## 5. Tecnologías utilizadas

- Java 25 y Spring Boot 4.1.0 para el desarrollo de los servicios REST.
- Spring Cloud y Netflix Eureka para la arquitectura distribuida y el descubrimiento de servicios.
- Spring Cloud Gateway como punto de entrada único.
- Spring Security, JJWT y BCrypt para seguridad, autenticación y cifrado de contraseñas.
- Spring Data JPA, H2, PostgreSQL y Flyway para persistencia y migraciones.
- OpenFeign para la comunicación del BFF con servicios internos.
- Swagger/OpenAPI para documentación interactiva de las APIs.
- Docker y Docker Compose para la construcción y orquestación de contenedores.
- JUnit 5, Mockito y JaCoCo para pruebas y cobertura.
- Sentry/GlitchTip para observabilidad y registro centralizado de errores.

## 6. Seguridad

El servicio Auth genera tokens JWT firmados con HMAC-SHA384. Las contraseñas se manejan con BCrypt. El login se realiza mediante el Gateway con la ruta POST http://localhost:8080/login.

Los endpoints protegidos utilizan el encabezado Authorization: Bearer <token_JWT>. Las credenciales de prueba disponibles son: usuario suer con contraseña 1234 y rol ROLE_USER; y usuario admin con contraseña password y rol ROLE_ADMIN.

## 7. Persistencia y datos iniciales

Cada microservicio mantiene una base de datos aislada. Flyway se utiliza para crear las tablas y cargar datos iniciales, lo que permite realizar pruebas funcionales inmediatamente después de levantar el proyecto.

Como ejemplo, ms-category administra la entidad Category con los campos id, name y description. El servicio permite listar, crear, consultar, actualizar y eliminar categorías.

## 8. Despliegue con Docker

El sistema se desplegó mediante Docker Compose. El siguiente comando construye las imágenes e inicia todos los contenedores:

```powershell
docker compose up --build -d
```

El estado se verifica con el comando docker compose ps. Para detener y eliminar los contenedores se utiliza docker compose down.

No se deben ejecutar simultáneamente los servicios con Gradle bootRun y Docker Compose, ya que ambos mecanismos ocuparían los mismos puertos locales.

## 9. Pruebas funcionales realizadas

Las pruebas se realizaron con la colección lexicon_postman_collection.json. Primero se ejecutó el login para obtener el token JWT. Posteriormente se probaron los endpoints CRUD de los microservicios a través de la URL base http://localhost:8080.

Se validaron operaciones de consulta, creación, actualización y eliminación. Como caso representativo, se realizó la creación y actualización de categorías en ms-category. Las respuestas obtenidas en Postman confirmaron el funcionamiento del Gateway, la autenticación JWT, el enrutamiento hacia los microservicios y las operaciones CRUD.

También se verificó que Eureka estuviera disponible en http://localhost:8761 y que los servicios estuvieran registrados correctamente.

## 10. Documentación de API

Cada microservicio publica una interfaz Swagger UI. Algunos accesos relevantes son: Auth en http://localhost:8081/swagger-ui/index.html, Libros en http://localhost:8082/swagger-ui/index.html, Categorías en http://localhost:8086/swagger-ui/index.html y Reportes en http://localhost:8090/swagger-ui/index.html.

La colección de Postman permite reproducir las pruebas de forma ordenada, guardando automáticamente el token de autenticación luego del login.

## 11. Observabilidad y manejo de errores

El proyecto usa SLF4J y Logback para el registro de eventos. Además, incorpora un Request-ID para correlacionar solicitudes entre servicios.

La integración con GlitchTip, compatible con Sentry, está preparada para enviar excepciones y logs a un sistema centralizado. Para utilizarla es necesario configurar un SENTRY_DSN válido. GlitchTip no se incluye como contenedor dentro del archivo docker-compose.yml.

## 12. Conclusiones

Lexicon demuestra una implementación funcional de arquitectura de microservicios aplicada al dominio de una biblioteca. La separación de responsabilidades permite que los servicios evolucionen de forma independiente, mientras que Eureka y el API Gateway proporcionan descubrimiento y acceso centralizado.

Las pruebas realizadas con Docker y Postman confirmaron que los servicios se levantan, se autentican mediante JWT y responden correctamente a las operaciones CRUD. Como mejora futura se propone incorporar una instancia propia de GlitchTip, utilizar PostgreSQL por servicio en producción y ejecutar pruebas de integración dentro de un proceso de integración continua.

## 13. Anexos

- Diagramas de arquitectura: Diagramas.md.
- Guía de ejecución: Ejecutar_Proyecto.md.
- Guía de Postman: como_usar_postman.md.
- Colección de pruebas: lexicon_postman_collection.json.
