# Arquitectura C3 - Lexicon Project

Este documento ilustra la arquitectura de microservicios de **Lexicon** utilizando el estándar de diagramas de contenedores (C4 Model) implementado a través de Mermaid.JS. 

## Diagrama de Contenedores

```mermaid
flowchart TD
    %% Estilos Profesionales
    classDef user fill:#2563EB,stroke:#1E40AF,stroke-width:2px,color:#FFF,rx:5px,ry:5px
    classDef gateway fill:#10B981,stroke:#047857,stroke-width:2px,color:#FFF,rx:5px,ry:5px
    classDef bff fill:#F59E0B,stroke:#B45309,stroke-width:2px,color:#FFF,rx:5px,ry:5px
    classDef service fill:#6366F1,stroke:#4338CA,stroke-width:2px,color:#FFF,rx:5px,ry:5px
    classDef registry fill:#8B5CF6,stroke:#6D28D9,stroke-width:2px,color:#FFF,rx:5px,ry:5px
    classDef db fill:#475569,stroke:#1E293B,stroke-width:2px,color:#FFF,rx:10px,ry:10px

    %% Nodos
    Client((👤 Cliente REST\nPostman/BFF Web)):::user

    subgraph "Lexicon Microservices Ecosystem"
        direction TB
        
        %% Infraestructura Core
        Gateway[API Gateway\nPuerto: 8080\nEnrutamiento y Seguridad]:::gateway
        Eureka[Eureka Server\nPuerto: 8761\nService Discovery]:::registry
        BFF[BFF Service\nPuerto: 8082\nOrquestador Backend For Frontend]:::bff
        
        %% Servicios de Dominio
        Auth[Auth Service\nPuerto: 8081\nGestión JWT y Usuarios]:::service
        Book[Book Service\nPuerto: 8083\nCatálogo de Libros]:::service
        Customer[Customer Service\nPuerto: 8084\nGestión de Clientes]:::service
        Loan[Loan Service\nPuerto: 8085\nGestión de Préstamos]:::service

        %% Bases de Datos (Aisladas por Servicio)
        DB_Auth[(H2 Auth)]:::db
        DB_Book[(H2 Book)]:::db
        DB_Cust[(H2 Customer)]:::db
        DB_Loan[(H2 Loan)]:::db
    end

    %% Flujos de Comunicación Cliente -> Gateway
    Client == "1. POST /login\n2. GET /api/v1/*\n(Bearer JWT)" ==> Gateway

    %% Flujos de Gateway -> Microservicios
    Gateway -- "Enruta a" --> Auth
    Gateway -- "Valida JWT & Enruta a" --> BFF
    Gateway -- "Valida JWT & Enruta a" --> Book
    Gateway -- "Valida JWT & Enruta a" --> Customer
    Gateway -- "Valida JWT & Enruta a" --> Loan

    %% Flujos del BFF (Orquestación Interna)
    BFF -. "Busca detalles del préstamo" .-> Loan
    BFF -. "Busca info del libro" .-> Book

    %% Flujos de Persistencia
    Auth --- DB_Auth
    Book --- DB_Book
    Customer --- DB_Cust
    Loan --- DB_Loan

    %% Flujos de Registro y Descubrimiento (Service Registry)
    Auth -.-|Registra| Eureka
    Book -.-|Registra| Eureka
    Customer -.-|Registra| Eureka
    Loan -.-|Registra| Eureka
    BFF -.-|Registra| Eureka
    Gateway -.-|Descubre Instancias| Eureka
```

## Flujos de Negocio Destacados

1. **Flujo de Autenticación**: El usuario envía credenciales (`/login`) al `API Gateway`. El Gateway ruteará la petición directamente al `Auth Service`, quien verifica la Base de Datos y devuelve un JWT firmado mediante `HMAC-SHA384`.
2. **Seguridad Centralizada**: Toda petición (excepto login) es interceptada por el `API Gateway`, que parsea y valida criptográficamente el JWT. Si es inválido, rechaza la petición (`401 Unauthorized`) antes de que toque los microservicios, aliviando la carga.
3. **Flujo del BFF**: Cuando se consulta un préstamo consolidado, el `BFF Service` hace peticiones asíncronas internas a `ms-loan` y `ms-book` para armar un payload complejo, devolviéndolo listo para que el Frontend lo consuma sin hacer múltiples peticiones.
4. **Service Discovery**: Ningún servicio conoce la IP del otro. Todos se registran en `Eureka` y se comunican resolviendo los nombres de registro (e.g. `http://MS-BOOK/`).
