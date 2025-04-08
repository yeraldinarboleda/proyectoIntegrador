package proymodpredictivoia.demo;

import org.apache.poi.sl.usermodel.ObjectMetaData.Application;
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

        String BD_url = dotenv.get("DB_URL");
        String BD_user = dotenv.get("DB_USER");
        String BD_pass = dotenv.get("DB_PASS");
        
        System.out.println("DB_URL: " + BD_url);
        System.out.println("DB_USER: " + BD_user);
        System.out.println("DB_PASS: " + BD_pass);


        if (apiGeminiKey == null || googleVisionApiKey == null) {
            throw new IllegalStateException("Las claves API_GEMINI_KEY o GOOGLE_VISION_API_KEY no están definidas en el archivo .env");
        }

        // Establecer las variables de entorno en el sistema
        System.setProperty("API_GEMINI_KEY", apiGeminiKey);
        System.setProperty("GOOGLE_VISION_API_KEY", googleVisionApiKey);

        


        if (BD_url == null || BD_user == null || BD_pass == null) {
            throw new IllegalStateException("Las claves de la base de datos no están definidas en el archivo .env");
        }

        System.setProperty("DB_URL",BD_url );
        System.setProperty("DB_USER",BD_user );
        System.setProperty("DB_PASS", BD_pass);

        // Iniciar la aplicación de Spring Boot
        SpringApplication.run(DemoApplication.class, args);
    }
}