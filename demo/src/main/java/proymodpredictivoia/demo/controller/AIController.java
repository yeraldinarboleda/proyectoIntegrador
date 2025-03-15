package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proymodpredictivoia.demo.ai.AIService;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    // Endpoint para procesar la imagen y generar el análisis con Gemini o responder preguntas
    @PostMapping("/generate")
    public ResponseEntity<String> generateFromImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "text", required = false) String inputText) {
        try {
            // Log para verificar los valores que llegan
            System.out.println("Texto proporcionado: " + inputText);
            if (file != null) {
                System.out.println("Archivo proporcionado: " + file.getOriginalFilename());
            }

            String extractedText = null;

            // Si se proporciona una imagen, extraer el texto
            if (file != null && !file.isEmpty()) {
                extractedText = aiService.extractTextFromImage(file);
            }

            // Verificar si se recibió texto extraído o texto proporcionado
            if ((extractedText == null || extractedText.isEmpty()) && (inputText == null || inputText.isEmpty())) {
                return ResponseEntity.badRequest().body("Se requiere texto proporcionado o texto extraído de la imagen.");
            }

            // Combinar el texto extraído con el texto proporcionado (si existe)
            String textToAnalyze = (inputText != null && !inputText.isEmpty()) ? inputText : extractedText;

            // Analizar el texto con Gemini
            String analysis = aiService.analyzeWithGemini(extractedText, inputText);

            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            // Log para depurar excepciones
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error procesando la solicitud: " + e.getMessage());
        }
    }
    @GetMapping("/models") // New endpoint to list models
    public ResponseEntity<String> listModels() {
        String modelsResponse = aiService.listGeminiModels();
        return ResponseEntity.ok(modelsResponse);
    }
}
