package proymodpredictivoia.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		        // Cargar variables de entorno desde el archivo .env
        Dotenv dotenv = Dotenv.load();

        // Establecer las variables de entorno en el sistema
        System.setProperty("API_GEMINI_KEY", dotenv.get("API_GEMINI_KEY"));

		SpringApplication.run(DemoApplication.class, args);
	}

}
