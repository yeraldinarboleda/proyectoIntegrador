package proymodpredictivoia.demo.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AIService {

    @Value("${API_GEMINI_KEY}")
    private String geminiApiKey;

    public String extractTextFromImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        ByteString imgBytes = ByteString.readFrom(imageFile.getInputStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

        try {
            System.out.println("Intentando crear ImageAnnotatorClient...");
            ImageAnnotatorClient vision = ImageAnnotatorClient.create();
            System.out.println("ImageAnnotatorClient creado exitosamente.");
            //...
        } catch (IOException e) {
            System.err.println("Error al crear ImageAnnotatorClient: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear ImageAnnotatorClient: " + e.getMessage());
        }

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> requests = Collections.singletonList(request);
            AnnotateImageResponse response = vision.batchAnnotateImages(requests).getResponses(0);

            if (response.hasError()) {
                throw new RuntimeException("Error en la API de Vision: " + response.getError().getMessage());
            }

            return response.getTextAnnotationsList().isEmpty() ? null : response.getTextAnnotationsList().get(0).getDescription();
        }
    }

    public String describeImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        ByteString imgBytes = ByteString.readFrom(imageFile.getInputStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).setMaxResults(10).build(); // Obtener hasta 10 etiquetas
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

        try {
            System.out.println("Intentando crear ImageAnnotatorClient...");
            ImageAnnotatorClient vision = ImageAnnotatorClient.create();
            System.out.println("ImageAnnotatorClient creado exitosamente.");
            //...
        } catch (IOException e) {
            System.err.println("Error al crear ImageAnnotatorClient: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear ImageAnnotatorClient: " + e.getMessage());
        }

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> requests = Collections.singletonList(request);
            AnnotateImageResponse response = vision.batchAnnotateImages(requests).getResponses(0);

            if (response.hasError()) {
                throw new RuntimeException("Error en la API de Vision: " + response.getError().getMessage());
            }

            StringBuilder description = new StringBuilder();
            for (EntityAnnotation label : response.getLabelAnnotationsList()) {
                description.append(label.getDescription()).append(", ");
            }
            return description.length() > 0 ? description.substring(0, description.length() - 2) : "No se encontraron etiquetas.";
        }
    }

    public String analyzeWithGemini(String extractedText, String imageDescription) throws IOException {
        WebClient webClient = WebClient.create();

        String prompt = "Eres un cardiologo experto el cual te encargas de analizar el siguiente texto extraido de una imagen y" 
        +"su descripcion, y dar una respuesta basada en el contenido de la imagen, asi como dar un diagnotico final,"+ 
        "tambien puedes responder preguntas sin necesidad de una imagen, solo con el texto, y dar un diagnostico final,"+ 
        "y dar una respuesta basada en el contenido del texto, Ademas tienes que decir que pasaria si no sigue las recomendaciones y que pasaria si sigue las recomendaciones.: " +
        "Texto extraido: " + (extractedText != null ? extractedText : "No se encontró texto.") +
        " Descripcion de la imagen: " + (imageDescription != null ? imageDescription : "No se encontró descripción.");

        String escapedPrompt = prompt.replace("\"", "\\\"");
        String requestBody = String.format("{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}", escapedPrompt);

        System.out.println("Request Body: " + requestBody);

        try {
            String response = webClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=" + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Raw Gemini Response: " + response);

            if (response != null && !response.isEmpty()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response);
                    JsonNode textNode = jsonNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");

                    if (textNode.isTextual()) {
                        return textNode.asText();
                    } else {
                        System.out.println("No text content found in Gemini response.");
                        return "No response from Gemini (text content not found).";
                    }

                } catch (Exception e) {
                    System.out.println("Error parsing Gemini response: " + e.getMessage());
                    return "Error parsing Gemini response.";
                }
            } else {
                System.out.println("Empty response from Gemini.");
                return "No response from Gemini (empty response).";
            }

        } catch (WebClientResponseException e) {
            System.out.println("Error en la solicitud a Gemini: " + e.getMessage());
            System.out.println("Response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error en la API Gemini: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado en la solicitud a Gemini: " + e.getMessage(), e);
        }
    }
}