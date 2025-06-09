# Usamos la imagen oficial de Maven para compilar el proyecto
FROM maven:3.8.7-eclipse-temurin-17 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el pom.xml y descargamos las dependencias primero (para cachear)
COPY pom.xml .

RUN mvn dependency:go-offline

# Copiamos el resto del código fuente
COPY src ./src

# Compilamos y empaquetamos el jar
RUN mvn clean package -DskipTests

# Segunda etapa: imagen más ligera para ejecutar la app
FROM eclipse-temurin:17-jre-alpine

# Copiamos el jar generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

COPY .env .env

# Puerto en que correrá la app
EXPOSE 8081

# Ejecutamos la app
ENTRYPOINT ["java", "-jar", "/app.jar"]
