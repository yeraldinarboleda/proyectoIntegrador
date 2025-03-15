package proymodpredictivoia.demo.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


@Service
public class AIService {

    @Value("${API_GEMINI_KEY}") // Inyecta la API Key desde .env
    private String geminiApiKey;


    // Extraer texto de la imagen usando Google Vision (sin cambios)
    public String extractTextFromImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null; // No hay imagen proporcionada
        }

        ByteString imgBytes = ByteString.readFrom(imageFile.getInputStream());

        // Crear la solicitud para Google Vision API
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

        // Crear cliente Vision API
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> requests = Collections.singletonList(request);
            AnnotateImageResponse response = vision.batchAnnotateImages(requests).getResponses(0);

            if (response.hasError()) {
                throw new RuntimeException("Error en la API de Vision: " + response.getError().getMessage());
            }

            return response.getTextAnnotationsList().isEmpty() ? null : response.getTextAnnotationsList().get(0).getDescription();
        }
    }

    // Llamar a Gemini para hacer el análisis del texto extraído o el texto directo

    public String analyzeWithGemini(String extractedText, String inputText) throws IOException {
        WebClient webClient = WebClient.create();

        if ((extractedText == null || extractedText.isEmpty()) && (inputText == null || inputText.isEmpty())) {
            throw new IllegalArgumentException("Se requiere texto proporcionado o texto extraído de la imagen.");
        }

        String prompt = extractedText != null ?
                "Eres un cardiologo experto el cual te encargas de analizar el siguiente texto extraido de una imagen y dar una respuesta basada en el contenido de la imagen, asi como un diagnotico final: " + extractedText :
                "Eres un cardiologo experto el cual puede dar una respuesta basada en una pregunta o texto proporcionado, asi como un diagnotico final: " + inputText;

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



    // **Helper method to extract text content from Gemini response (basic string manipulation)**
    private String extractTextContent(String jsonResponse) {
        // **IMPORTANT:** This is a VERY basic and fragile JSON parsing.
        // For production, use a proper JSON library like Jackson or Gson.
        try {
            int startMarkerIndex = jsonResponse.indexOf("\"text\": \"");
            if (startMarkerIndex != -1) {
                int textStartIndex = startMarkerIndex + 9; // Length of "\"text\": \""
                int textEndIndex = jsonResponse.indexOf("\"", textStartIndex);
                if (textEndIndex != -1) {
                    return jsonResponse.substring(textStartIndex, textEndIndex);
                }
            }
        } catch (Exception e) {
            System.err.println("Error during basic text extraction: " + e.getMessage());
        }
        return null; // Text content not found or error during extraction
    }

    // Method to list available Gemini models
    public String listGeminiModels() {
        WebClient webClient = WebClient.create();

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("generativelanguage.googleapis.com")
                            .path("/v1beta/models/**models/gemini-1.5-pro-latest**:generateContent") // Endpoint for listing models
                            .queryParam("key", geminiApiKey)
                            .build())
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("List Models Response: " + response); // Print the raw response
            return response; // Return the raw JSON response for now

        } catch (WebClientResponseException e) {
            System.err.println("Error listing Gemini models: " + e.getMessage());
            System.err.println("List Models Response body: " + e.getResponseBodyAsString());
            return "Error listing models: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            System.err.println("Unexpected error listing Gemini models: " + e.getMessage());
            return "Unexpected error listing models: " + e.getMessage();
        }
    }

}
