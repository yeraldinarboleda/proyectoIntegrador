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
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;

import org.apache.tika.exception.TikaException;

@Service
public class AIService {

    private Tika tika = new Tika();

    // Función para procesar imágenes con Google Cloud Vision API
    private String processImageWithGoogleVision(MultipartFile file) throws IOException {
        // Convertir la imagen a base64
        byte[] imageBytes = file.getBytes();
        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        // Crear la solicitud a Google Vision API
        String apiKey = System.getProperty("GOOGLE_VISION_API_KEY"); // Usa tu clave API aquí
        String visionApiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;

        String requestBody = "{ \"requests\": [{ \"image\": { \"content\": \"" + imageBase64 + "\" }, \"features\": [{ \"type\": \"TEXT_DETECTION\" }] }] }";

        // Configurar la solicitud HTTP
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Enviar la solicitud y recibir la respuesta
        ResponseEntity<String> response = restTemplate.exchange(visionApiUrl, HttpMethod.POST, entity, String.class);

        // Extraer el texto detectado de la respuesta
        // Aquí puedes personalizar para extraer partes específicas del JSON si lo deseas.
        return response.getBody();
    }

    public String generateContent(String text, List<MultipartFile> files) {
        StringBuilder extractedText = new StringBuilder(text);  // Combina el texto de entrada y lo extraído

        // Procesar archivos si los hay
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    // Procesar PDFs con Tika
                    if (file.getOriginalFilename().endsWith(".pdf")) {
                        extractedText.append(tika.parseToString(file.getInputStream())).append("\n");
                    }
                    // Procesar imágenes con Google Vision
                    else if (file.getOriginalFilename().endsWith(".jpg") || file.getOriginalFilename().endsWith(".png")) {
                        String visionText = processImageWithGoogleVision(file);
                        extractedText.append(visionText).append("\n");
                    } else {
                        extractedText.append("Unsupported file type: ").append(file.getOriginalFilename()).append("\n");
                    }
                } catch (IOException | TikaException e) {
                    e.printStackTrace();
                }
            }
        }

        // Generar el prompt para la IA (con el texto extraído)
        String prompt = "Eres un cardiólogo experto. Basándote en la siguiente información, analiza los documentos adjuntos (PDF, imágenes, radiografías) y proporciona un diagnóstico final detallado: " + extractedText.toString();

        // Llamar a la API de IA (puedes mantener tu lógica actual para esta parte)
        RestTemplate restTemplate = new RestTemplate();
        String apiKey = System.getProperty("API_GEMINI_KEY"); // Si usas Gemini IA
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        // Crear la solicitud
        String requestBody = "{ \"contents\": [{ \"parts\":[{\"text\": \"" + prompt + "\"}] }] }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Enviar la solicitud y obtener la respuesta
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // Procesar la respuesta de la IA
        String result = response.getBody();

        // Generar el informe en PDF
        PdfGenerator pdfGenerator2 = new PdfGenerator();
        try {
            pdfGenerator2.generatePdf(result, "output.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
