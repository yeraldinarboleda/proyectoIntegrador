package proymodpredictivoia.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // Cargar variables de entorno desde el archivo .env
        Dotenv dotenv = Dotenv.load();

        String apiGeminiKey = dotenv.get("API_GEMINI_KEY");
        String googleVisionApiKey = dotenv.get("GOOGLE_VISION_API_KEY");

        if (apiGeminiKey == null || googleVisionApiKey == null) {
            throw new IllegalStateException("Las claves API_GEMINI_KEY o GOOGLE_VISION_API_KEY no están definidas en el archivo .env");
        }

        // Establecer las variables de entorno en el sistema
        System.setProperty("API_GEMINI_KEY", apiGeminiKey);
        System.setProperty("GOOGLE_VISION_API_KEY", googleVisionApiKey);

        // Iniciar la aplicación de Spring Boot
        SpringApplication.run(DemoApplication.class, args);
    }
}
