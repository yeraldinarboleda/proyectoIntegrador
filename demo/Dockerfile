# Imagen base con Java 17
FROM openjdk:17-jdk-slim

# Directorio de trabajo dentro del contenedor
WORKDIR /app


# Copiar el JAR generado desde el build de Maven
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Copiar el archivo .env
COPY .env .env

# Exponer el puerto (ajusta si tu app usa otro puerto)
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
