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

    @PostMapping("/generate")
    public ResponseEntity<String> generateFromImageOrText(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "text", required = false) String text) {
        try {
            System.out.println("File received: " + (file != null ? file.getOriginalFilename() : "null"));
            System.out.println("Text received: " + text);
            if (file == null && (text == null || text.isEmpty())) {
                return ResponseEntity.badRequest().body("Se requiere una imagen o texto.");
            }

            if (file != null && !file.isEmpty()) {
                // Procesar la imagen
                String extractedText = aiService.extractTextFromImage(file);
                String imageDescription = aiService.describeImage(file);
                String analysis = aiService.analyzeWithGemini(extractedText, imageDescription);
                return ResponseEntity.ok(analysis);
            } else {
                // Procesar el texto
                String analysis = aiService.analyzeWithGemini(null, text);
                return ResponseEntity.ok(analysis);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error procesando la solicitud: " + e.getMessage());
        }
    }
}