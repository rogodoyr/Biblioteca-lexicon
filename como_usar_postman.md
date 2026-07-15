# Como Usar la Coleccion Postman de Lexicon

## 1. Importar la Coleccion

1. Abrir Postman
2. Clic en **Import** (boton arriba a la izquierda)
3. Seleccionar **File** y buscar `lexicon_postman_collection.json` en la raiz del proyecto
4. Clic en **Import**

La coleccion aparecera como **"Lexicon - Biblioteca Distribuida"** en la barra lateral izquierda.

## 2. Obtener el Token JWT (Paso Obligatorio)

Antes de probar cualquier endpoint, debes ejecutar el Login:

1. Abrir la carpeta **"0. Auth"**
2. Ejecutar **"Login - Obtener Token"** (clic en el boton Send)
3. Verificar que la respuesta sea:
```json
{
    "token": "eyJhbGciOiJIUzM4NCJ9..."
}
```
4. El token se guarda **automaticamente** en la variable `jwt_token` (gracias al script Test incluido)

> **Importante:** Si ves error 401 en los demas endpoints, significa que el token expiro o no se guardo. Vuelve a ejecutar el Login.

## 3. Probar los Endpoints

Una vez obtenido el token, puedes ejecutar cualquier request en cualquier orden. Todos los endpoints ya incluyen el header `Authorization: Bearer {{jwt_token}}` automaticamente.

### Estructura de Cada Servicio

Cada carpeta de servicio tiene 5 operaciones CRUD:

| Operacion | Metodo | Descripcion |
|-----------|--------|-------------|
| GET All | `GET` | Lista todos los registros |
| GET By ID | `GET` | Obtiene un registro especifico por ID |
| POST | `POST` | Crea un nuevo registro |
| PUT | `PUT` | Actualiza un registro existente |
| DELETE | `DELETE` | Elimina un registro |

### Orden Recomendado de Prueba

```
1. Auth → Login (obtener token)
2. MS-Book → POST (crear libro nuevo)
3. MS-Book → GET All (verificar que se creo)
4. MS-Book → GET By ID (buscar el libro creado)
5. MS-Book → PUT (actualizar el libro)
6. MS-Book → DELETE (eliminar el libro)
7. Repetir para los demas servicios...
```

## 4. Servicios Incluidos

| # | Servicio | Puerto | Descripcion |
|---|----------|--------|-------------|
| 0 | Auth | 8080 (gateway) | Login y JWT |
| 1 | MS-Book | 8082 | Gestion de libros |
| 2 | MS-Loan | 8083 | Gestion de prestamos |
| 3 | MS-Customer | 8084 | Gestion de clientes |
| 4 | MS-Category | 8086 | Categorias de libros |
| 5 | MS-Reservation | 8087 | Reservas de libros |
| 6 | MS-Notification | 8088 | Notificaciones |
| 7 | MS-Penalty | 8089 | Multas y penalizaciones |
| 8 | MS-Report | 8090 | Reportes y analiticas |
| 9 | BFF | 8085 | Backend For Frontend |
| 10 | Eureka | 8761 | Service Discovery |

## 5. Variables de Entorno

La coleccion usa estas variables:

| Variable | Valor | Descripcion |
|----------|-------|-------------|
| `base_url` | `http://localhost:8080` | URL del API Gateway |
| `jwt_token` | *(se llena con el Login)* | Token JWT de autenticacion |

Para cambiar la URL base (por ejemplo, si despliegas en otro servidor):
1. Clic en la coleccion → pestana **Variables**
2. Cambiar el valor de `base_url`

## 6. Solucion de Problemas

### Error 401 Unauthorized
- El token expiro o no se guardo
- Solucion: Ejecutar **"Login - Obtener Token"** de nuevo

### Error 404 Not Found
- El recurso no existe
- Solucion: Primero ejecutar POST para crear un registro, luego usar su ID

### Error 500 Internal Server Error
- Error en el servidor
- Solucion: Verificar que todos los servicios estan corriendo con `docker compose ps`

### Error Connection Refused
- Los servicios no estan corriendo
- Solucion: `docker compose up --build -d`

### Token no se guarda automaticamente
- Verificar que el script Test este configurado en el request Login
- El script debe aparecer en la pestana **Tests** del request

## 7. Ejemplo Flujo Completo

```
1. POST /login → Obtener token
2. POST /api/v1/books → Crear libro "Cien Anos de Soledad"
   → Respuesta: { "id": 4, "title": "Cien Anos de Soledad", ... }
3. GET /api/v1/books → Ver todos los libros (deberia haber 4)
4. GET /api/v1/books/4 → Obtener el libro creado
5. PUT /api/v1/books/4 → Actualizar titulo
6. DELETE /api/v1/books/4 → Eliminar el libro
7. GET /api/v1/books → Verificar que quedan 3 libros
```
