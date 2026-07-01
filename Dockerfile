# Imagen reutilizable para todos los microservicios.
# Se indica el modulo a compilar con el argumento MODULE (eureka, auth, product, store, apigateway).
FROM eclipse-temurin:25-jdk AS build

ARG MODULE
WORKDIR /workspace

COPY ${MODULE}/ ./
RUN sed -i -e 's/\r$//' gradlew && chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
