# 📚 Lexicon - Sistema de Biblioteca Distribuida

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=spring-boot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2025.1.2-6DB33F?style=for-the-badge&logo=spring)
![JWT](https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=json-web-tokens)
![Gradle](https://img.shields.io/badge/Gradle-Build_Tool-02303A?style=for-the-badge&logo=gradle)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka-Server-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)


**Lexicon** es una arquitectura moderna basada en microservicios diseñada para gestionar el ecosistema completo de una biblioteca (Libros, Clientes, Préstamos). Implementa los más altos estándares de escalabilidad y seguridad usando el stack de Spring Cloud.

---

## 🏗️ Arquitectura del Sistema

El proyecto sigue un diseño de **Microservicios Aislados** con descubrimiento de red dinámico y seguridad perimetral.

📌 **[Ver el Diagrama de Arquitectura Detallado (C4)](./diagramaC4.md)**

### Componentes Principales:
1. 🛡️ **API Gateway (`apigateway`)**: Actúa como el punto de entrada único al sistema. Maneja la validación de tokens JWT de forma centralizada.
2. 📖 **Registro Eureka (`eureka`)**: Service Discovery. Mantiene un registro en tiempo real de qué microservicios están en vivo y en qué puertos se encuentran.
3. 🔐 **Auth Service (`auth`)**: Responsable de la autenticación de usuarios. Emite tokens JWT mediante encriptación BCrypt y firma HMAC-SHA384.
4. 📚 **Book Service (`ms-book`)**: Microservicio CRUD para la gestión del inventario de libros.
5. 👥 **Customer Service (`ms-customer`)**: Microservicio CRUD para la gestión de los clientes/lectores.
6. 🤝 **Loan Service (`ms-loan`)**: Microservicio transaccional para la gestión de préstamos de libros.
7. 🧩 **BFF Service (`bff`)**: (Backend For Frontend) Microservicio orquestador que consume datos de Book y Loan para devolver información consolidada a las aplicaciones cliente.

---

## 🛠️ Tecnologías y Stack Técnico

- **Core**: Java 25, Spring Boot 4.1.0.
- **Cloud & Orchestration**: Spring Cloud Netflix Eureka, Spring Cloud Gateway.
- **Seguridad**: Spring Security, JSON Web Tokens (JJWT 0.12.6), BCryptPasswordEncoder.
- **Persistencia**: Spring Data JPA, Base de datos en memoria H2.
- **Migraciones de BD**: Flyway (Control de versiones de la base de datos).
- **Pruebas y QA**: JUnit 5, Mockito, JaCoCo (100% de cobertura en servicios clave), Shell Scripts para Integration Testing.
- **Utilidades**: Lombok (Reducción de boilerplate), OpenAPI/Swagger.

---

## 🚀 Cómo levantar el entorno local

El proyecto incluye scripts Bash automatizados para facilitar la experiencia de desarrollo.

### 1. Iniciar el Ecosistema
Abre una terminal Git Bash (o similar) en la raíz del proyecto y ejecuta:
```bash
./start-services.sh
```
*Este script levantará primero Eureka, luego el API Gateway, Auth y progresivamente los demás microservicios.*

### 2. Verificar el estado
Entra a tu navegador web y visita:
👉 **http://localhost:8761** (Panel de Control de Eureka)
Debes visualizar 6 instancias registradas (`APIGATEWAY`, `AUTH`, `BFF`, `MS-BOOK`, `MS-CUSTOMER`, `MS-LOAN`).

### 3. Detener el ecosistema
Para detener todos los servicios y liberar los puertos:
```bash
./stop-services.sh
```

---

## 🧪 Pruebas y Consumo de la API

Lexicon ha sido configurado para ser probado sin fricciones directamente desde **Visual Studio Code** utilizando la extensión **REST Client**.

En la carpeta raíz de cada microservicio, encontrarás un archivo `api.http` con peticiones pre-configuradas.

**Flujo de prueba recomendado:**
1. Abre el archivo `auth/api.http`.
2. Haz clic en `Send Request` sobre el bloque de **Login**.
3. *Automáticamente*, VSCode guardará el Token devuelto.
4. Abre `ms-book/api.http`, repite el paso de login si es necesario, y luego usa los endpoints CRUD, los cuales inyectarán el token automáticamente en la cabecera `Authorization`.

**Endpoint Principal:** Todas las peticiones deben ser apuntadas al API Gateway (`http://localhost:8080`), el cual se encarga de enrutarlas internamente mediante Eureka.

---

## 📊 Calidad de Código y Testing Continuo

Se ha implementado una robusta suite de tests. Para ejecutar las pruebas unitarias y verificar la cobertura con JaCoCo:

```bash
cd ms-book  # o el servicio que desees probar
./gradlew test jacocoTestReport
```

Para una validación E2E (End-to-End) de toda la comunicación de red, inicio de sesión y consumo de APIs, ejecuta el script en la raíz del proyecto:
```bash
./integration-test.sh
```

---
*Desarrollado como proyecto de FullStack Architecture* 🚀
