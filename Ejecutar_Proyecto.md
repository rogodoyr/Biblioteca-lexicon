# Guia para Ejecutar el Proyecto Lexicon

## 1. Levantar Todo el Proyecto

```bash
cd Biblioteca-lexicon-Rama-Axel

# Opcion A: Docker (recomendado para ver todo junto)
docker compose up --build

# Opcion B: Local con Gradle (necesitas Java 25)
./start-services.sh
```

## 2. Verificar que Funciona

```bash
# Ver Eureka (deberian aparecer 11 servicios registrados)
# Abrir en navegador: http://localhost:8761

# Hacer login para obtener token JWT
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"suer","password":"1234"}'

# Copiar el token de la respuesta y usarlo en los demas endpoints
```

## 3. Probar Swagger (OpenAPI)

Cada servicio tiene Swagger UI habilitado y accesible directamente en su puerto:

```bash
# Auth - Login y gestion de usuarios
http://localhost:8081/swagger-ui/index.html

# ms-book - CRUD de libros
http://localhost:8082/swagger-ui/index.html

# ms-loan - CRUD de prestamos
http://localhost:8083/swagger-ui/index.html

# ms-customer - CRUD de clientes
http://localhost:8084/swagger-ui/index.html

# bff - Backend For Frontend
http://localhost:8085/swagger-ui/index.html

# ms-category - CRUD de categorias
http://localhost:8086/swagger-ui/index.html

# ms-reservation - CRUD de reservas
http://localhost:8087/swagger-ui/index.html

# ms-notification - CRUD de notificaciones
http://localhost:8088/swagger-ui/index.html

# ms-penalty - CRUD de multas
http://localhost:8089/swagger-ui/index.html

# ms-report - CRUD de reportes
http://localhost:8090/swagger-ui/index.html
```

> Cada Swagger UI muestra los endpoints de ese servicio. Usa el endpoint `/login` primero para obtener un token, luego prueba los demas endpoints desde ahi.

### Verificar Swagger funciona

```bash
# Test rapido de que la API docs funciona
curl http://localhost:8082/v3/api-docs | head -1
# Deberia retornar: {"openapi":"3.1.0",...}
```

## 4. Probar con VS Code REST Client

Abre los archivos `api.http` en cada carpeta de servicio:

```
auth/api.http          # Login primero aqui
ms-book/api.http       # CRUD de libros
ms-loan/api.http       # CRUD de prestamos
ms-customer/api.http   # CRUD de clientes
ms-category/api.http   # CRUD de categorias
ms-reservation/api.http
ms-notification/api.http
ms-penalty/api.http
ms-report/api.http
```

En VS Code: clic derecho sobre el bloque `###` → "Send Request"

## 5. Ver Logs

```bash
# Docker: logs de todos los servicios
docker compose logs -f

# Docker: logs de un servicio especifico
docker compose logs -f ms-book
docker compose logs -f apigateway

# Docker: ultimas 100 lineas de todos
docker compose logs --tail=100

# Local (sin Docker): los logs estan en .run/logs/
tail -f .run/logs/ms-book.log
tail -f .run/logs/apigateway.log
```

## 6. Probar GlitchTip

GlitchTip ya esta integrado pero no esta conectado a un servidor porque `SENTRY_DSN` esta vacio. Para activarlo:

### Paso 1: Tener un servidor GlitchTip corriendo

```bash
# Si tienes GlitchTip instalado, obtener el DSN desde:
# GlitchTip → Settings → Client Keys (DSN)
# Formato: https://<public-key>@tu-dominio/<project-id>
```

### Paso 2: Configurar la variable de entorno

```bash
# Docker: agregar al docker-compose.yml en cada servicio
environment:
  SENTRY_DSN: "https://tu-key@tu-glitchtip-server/1"
  SENTRY_ENVIRONMENT: development

# O al correr con Docker:
docker compose up -e SENTRY_DSN="https://tu-key@tu-glitchtip-server/1"
```

### Paso 3: Verificar que funciona

```bash
# Generar un error intencional para probar:
curl http://localhost:8080/api/v1/categories/99999

# En GlitchTip deberia aparecer el error en la seccion Issues
# Los logs INFO aparecen en la seccion Logs de GlitchTip
```

### Sin servidor GlitchTip

Todo funciona normalmente, solo no envia datos a ningun lado. La aplicacion no falla por tener `SENTRY_DSN` vacio.

## 7. Endpoints Principales (via API Gateway en puerto 8080)

```
POST   /login                          → Login y obtener token
GET    /api/v1/books                   → Listar libros
GET    /api/v1/books/{id}              → Libro por ID
POST   /api/v1/books                   → Crear libro
GET    /api/v1/loans                   → Listar prestamos
GET    /api/v1/customers               → Listar clientes
GET    /api/v1/categories              → Listar categorias
GET    /api/v1/reservations            → Listar reservas
GET    /api/v1/notifications            → Listar notificaciones
GET    /api/v1/penalties               → Listar multas
GET    /api/v1/reports                 → Listar reportes
GET    /eureka/apps                    → Ver servicios registrados
```

Todos los endpoints (excepto `/login`) requieren header:
```
Authorization: Bearer <tu-token>
```

## 8. Detener Todo

```bash
# Docker
docker compose down

# Local
./stop-services.sh
```

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
