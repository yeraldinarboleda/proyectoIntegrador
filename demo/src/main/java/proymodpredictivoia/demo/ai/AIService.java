package proymodpredictivoia.demo.ai;

import org.apache.tika.Tika;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import proymodpredictivoia.demo.ai.PdfGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import org.apache.tika.exception.TikaException;

@Service
public class AIService {

    private Tika tika = new Tika();

    // Procesar imágenes con Google Cloud Vision API
    private String processImageWithGoogleVision(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        // API Key de Google Vision (asegúrate de que esté configurada correctamente)
        String apiKey = System.getenv("GOOGLE_VISION_API_KEY");
        String visionApiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;

        // Crear el cuerpo de la solicitud
        String requestBody = "{ \"requests\": [{ \"image\": { \"content\": \"" + imageBase64 + "\" }, \"features\": [{ \"type\": \"TEXT_DETECTION\" }] }] }";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Enviar la solicitud y recibir la respuesta
        ResponseEntity<String> response = restTemplate.exchange(visionApiUrl, HttpMethod.POST, entity, String.class);

        return response.getBody(); // Aquí deberías parsear y extraer solo el texto relevante
    }

    // Método para combinar texto con la IA y procesar archivos
    public String generateContent(String text, List<MultipartFile> files) {
        StringBuilder extractedText = new StringBuilder(text);  // Combina el texto de entrada y lo extraído

        // Procesar archivos si los hay
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    // Procesar las imágenes con Google Cloud Vision
                    String visionResponse = processImageWithGoogleVision(file);
                    extractedText.append("\n").append("Texto extraído de la imagen: ").append(visionResponse);
                } catch (IOException e) {
                    extractedText.append("\n").append("Error al procesar el archivo: ").append(file.getOriginalFilename());
                }
            }
        }

        // Retorna el texto extraído (texto base + resultados de IA)
        return extractedText.toString();
    }
}

