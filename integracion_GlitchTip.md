# Integracion GlitchTip / Sentry - Guia Completa

## Que es GlitchTip

GlitchTip es una plataforma open-source de error tracking y logging compatible con el SDK de Sentry. Permite monitorear errores y logs de la aplicacion en tiempo real desde un dashboard web.

---

## Arquitectura de la Integracion

```
Controller (log.info / log.error)
       |
       |---> Logback + SentryAppender ---> GlitchTip Logs
       |
       +---> Excepcion ---> GlobalExceptionHandler
                                    |
                                    +---> GlitchTipErrorReporter ---> GlitchTip Issues
```

### Flujo de Datos

1. **Logs normales**: SLF4J -> Logback -> SentryAppender -> GlitchTip (seccion Logs)
2. **Excepciones**: GlobalExceptionHandler -> GlitchTipErrorReporter -> Sentry -> GlitchTip (seccion Issues)
3. **Request-ID**: RequestIdFilter genera X-Request-Id -> MDC -> todos los logs incluyen el ID

---

## Componentes Implementados (10 servicios)

### Dependencias (build.gradle)

```groovy
implementation platform('io.sentry:sentry-bom:8.+')
implementation 'io.sentry:sentry-spring-boot-4'
implementation 'io.sentry:sentry-logback'
```

### Clases GlitchTip (por cada servicio)

| Clase | Paquete | Funcion |
|-------|---------|---------|
| **GlitchTipErrorReporter** | glitchtip/ | Punto central para enviar excepciones y mensajes a GlitchTip |
| **GlitchTipLogger** | glitchtip/ | Wrapper de SLF4J + Sentry.logger() para envio explicito de logs |
| **SentryLifecycle** | glitchtip/ | Llama a Sentry.flush() al apagar la aplicacion |

### Clases Tracing (por cada servicio)

| Clase | Paquete | Funcion |
|-------|---------|---------|
| **RequestIdFilter** | tracing/ | Filtro HTTP que genera/reutiliza X-Request-Id |
| **RequestIdContext** | tracing/ | Holder MDC del requestId para correlacion |
| **RequestIdHeaders** | tracing/ | Constantes de headers (X-Request-Id, X-Correlation-Id) |
| **RequestIdConfig** | tracing/ | RestClient interceptor para propagar requestId en llamadas salientes |
| **RequestIdBeforeSendCallback** | tracing/ | Enriquece eventos Sentry con requestId |
| **RequestIdEventSupport** | tracing/ | Helper para marcar requestId en mensajes de error |

### Servicios con GlitchTip Integrado

| Servicio | Dependencias | Config | Clases | Estado |
|----------|--------------|--------|--------|--------|
| ms-book | ✅ | ✅ | ✅ | COMPLETO |
| ms-loan | ✅ | ✅ | ✅ | COMPLETO |
| ms-customer | ✅ | ✅ | ✅ | COMPLETO |
| ms-category | ✅ | ✅ | ✅ | COMPLETO |
| ms-reservation | ✅ | ✅ | ✅ | COMPLETO |
| ms-notification | ✅ | ✅ | ✅ | COMPLETO |
| ms-penalty | ✅ | ✅ | ✅ | COMPLETO |
| ms-report | ✅ | ✅ | ✅ | COMPLETO |
| auth | ✅ | ✅ | ✅ | COMPLETO |
| bff | ✅ | ✅ | ✅ | COMPLETO |

---

## Configuracion

### application.properties (servicios con .properties)

```properties
# GlitchTip (Sentry-compatible)
sentry.dsn=${SENTRY_DSN:}
sentry.release=${SENTRY_RELEASE:0.0.1-SNAPSHOT}
sentry.environment=${SENTRY_ENVIRONMENT:development}
sentry.traces-sample-rate=${SENTRY_TRACES_SAMPLE_RATE:0.1}
sentry.send-default-pii=false

# Enviar logs de SLF4J/Logback a GlitchTip (seccion Logs)
sentry.logs.enabled=true
sentry.logging.minimum-level=info
sentry.logging.minimum-event-level=error
sentry.logging.minimum-breadcrumb-level=debug

# Enviar excepciones manejadas por @ExceptionHandler
sentry.exception-resolver-order=-2147483647

# Incluir requestId del MDC en logs enviados a GlitchTip
sentry.logging.context-tags=requestId

# Request ID en logs de consola
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] [requestId=%X{requestId}] %-5level %logger{36} - %msg%n
```

### application.yml (ms-customer, bff)

```yaml
sentry:
  dsn: ${SENTRY_DSN:}
  release: ${SENTRY_RELEASE:0.0.1-SNAPSHOT}
  environment: ${SENTRY_ENVIRONMENT:development}
  traces-sample-rate: ${SENTRY_TRACES_SAMPLE_RATE:0.1}
  send-default-pii: false
  logs:
    enabled: true
  logging:
    minimum-level: info
    minimum-event-level: error
    minimum-breadcrumb-level: debug
    context-tags: requestId
  exception-resolver-order: -2147483647
```

### Variables de Entorno

| Variable | Valor por defecto | Descripcion |
|----------|-------------------|-------------|
| `SENTRY_DSN` | *(vacia)* | DSN del proyecto en GlitchTip |
| `SENTRY_RELEASE` | `0.0.1-SNAPSHOT` | Version de la aplicacion |
| `SENTRY_ENVIRONMENT` | `development` | Entorno (development/staging/production) |
| `SENTRY_TRACES_SAMPLE_RATE` | `0.1` | Porcentaje de traces a enviar |

---

## Como Activar GlitchTip

### Paso 1: Crear Cuenta

1. Ir a **https://glitchtip.com**
2. Crear cuenta gratuita
3. Crear un proyecto nuevo (seleccionar "Sentry" como tipo de SDK)
4. Ir a **Settings → Client Keys (DSN)**
5. Copiar el DSN (formato: `https://<public-key>@<tu-servidor>/<project-id>`)

### Paso 2: Configurar DSN

**Opcion A: Docker (editar docker-compose.yml)**

Agregar en cada servicio:

```yaml
environment:
  SENTRY_DSN: "https://tu-key@tu-glitchtip-server/1"
  SENTRY_ENVIRONMENT: development
```

**Opcion B: Terminal**

```bash
export SENTRY_DSN="https://tu-key@tu-glitchtip-server/1"
export SENTRY_ENVIRONMENT=development
```

**Opcion C: Archivo .env**

```bash
# Crear archivo .env en la raiz del proyecto
SENTRY_DSN=https://tu-key@tu-glitchtip-server/1
SENTRY_ENVIRONMENT=development
```

### Paso 3: Reiniciar Servicios

```bash
docker compose down
docker compose up --build
```

### Paso 4: Probar

```bash
# Generar un error (aparecera en GlitchTip Issues)
curl http://localhost:8080/api/v1/categories/99999

# Generar logs (aparecera en GlitchTip Logs)
curl http://localhost:8080/api/v1/books
```

---

## Como Usar en el Codigo

### Opcion 1: Solo SLF4J (recomendado para logs normales)

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(MiClase.class);

// Logs que se envian a GlitchTip si minimum-level lo permite
logger.info("Operacion completada");
logger.warn("Algo inesperado: {}", detalle);
logger.error("Error occurred", exception);
```

### Opcion 2: SLF4J + Sentry.logger() explicito (recomendado para GlitchTip)

```java
import com.lexicon.mi_servicio.glitchtip.GlitchTipLogger;

@Autowired
private GlitchTipLogger glitchTipLogger;

// Envio explicito a GlitchTip Logs
glitchTipLogger.info(logger, "Mensaje visible en GlitchTip Logs");
glitchTipLogger.warn(logger, "Algo inesperado: {}", detalle);
glitchTipLogger.error(logger, "Error occurred", exception);
```

### Opcion 3: Envio centralizado de errores

```java
import com.lexicon.mi_servicio.glitchtip.GlitchTipErrorReporter;
import io.sentry.SentryLevel;

@Autowired
private GlitchTipErrorReporter errorReporter;

// Capturar excepcion
try {
    // logica
} catch (Exception e) {
    errorReporter.captureException(e);
    errorReporter.captureException(e, "Contexto adicional del error");
}

// Capturar mensaje
errorReporter.captureMessage("Algo relevante paso", SentryLevel.WARNING);

// Agregar breadcrumb (trail de eventos)
errorReporter.addBreadcrumb("Usuario hizo clic en boton");
```

### Opcion 4: En GlobalExceptionHandler (ya implementado)

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
    // Automaticamente reporta a GlitchTip
    errorReporter.captureException(ex, "Excepcion no controlada en la API");
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "error", ex.getMessage(),
                "requestId", RequestIdContext.getOrUnknown()));
}
```

---

## Request-ID entre Microservicios

Cada request HTTP recibe un `X-Request-Id` para correlacionar logs y errores entre servicios.

### Flujo

```
Cliente / API Gateway
    |  X-Request-Id: abc-123 (opcional)
    v
RequestIdFilter -> genera o reutiliza ID -> MDC + GlitchTip tag
    |
    +---> Logs SLF4J: [requestId=abc-123]
    +---> GlitchTip Issues/Logs: tag request_id=abc-123
    +---> RestClient saliente: propaga X-Request-Id al siguiente microservicio
```

### Headers

| Header | Uso |
|--------|-----|
| `X-Request-Id` | ID principal de correlacion |
| `X-Correlation-Id` | Fallback si no viene X-Request-Id |

### Buscar por Request-ID en GlitchTip

En la seccion **Issues** de GlitchTip:
```
is:unresolved <request-id>
```

En la seccion **Logs**:
```
requestId:<tu-request-id>
```

---

## Niveles de Log en GlitchTip

| Propiedad | Valor | Efecto |
|-----------|-------|--------|
| `sentry.logging.minimum-level` | `info` | Logs INFO+ van a seccion **Logs** de GlitchTip |
| `sentry.logging.minimum-event-level` | `error` | Solo errores van a seccion **Issues** |
| `sentry.logging.minimum-breadcrumb-level` | `debug` | Debug van a breadcrumbs en issues |

---

## Sin Servidor GlitchTip

Si `SENTRY_DSN` esta vacia (valor por defecto):
- La aplicacion **funciona normalmente**
- No envia datos a ningun lado
- No hay errores en consola
- Solo falta el monitoreo remoto

---

## Troubleshooting

### Error: "No se ven Issues en GlitchTip"
1. Verificar que `SENTRY_DSN` este configurado correctamente
2. Activar debug: `sentry.debug=true` en application.properties
3. Verificar en consola: `DEBUG: Envelope sent successfully.`

### Error: "No se ven Logs en GlitchTip"
1. Verificar `sentry.logs.enabled=true`
2. Verificar `sentry.logging.minimum-level=info`
3. Verificar que el worker de GlitchTip este corriendo

### Error: "Connection refused"
1. Verificar que el servidor GlitchTip este accesible
2. Verificar que el DSN apunte al servidor correcto
3. Verificar firewall/networking
