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
        if ((file == null || file.isEmpty()) && (text == null || text.isEmpty())) {
            return ResponseEntity.badRequest().body("Se requiere una imagen o texto.");
        }

        if (file != null && !file.isEmpty()) {
            // Procesar la imagen: extraer texto e interpretar la imagen
            String extractedText = aiService.extractTextFromImage(file);
            String imageDescription = aiService.describeImage(file);

            // Combina el texto extraído del archivo con el texto ingresado (si existe)
            String combinedText = "";
            if (text != null && !text.isEmpty()) {
                combinedText = text + "\n" + extractedText;
            } else {
                combinedText = extractedText;
            }
            
            // Se envía la información combinada y la descripción de la imagen a la IA
            String analysis = aiService.analyzeWithGemini(combinedText, imageDescription);
            return ResponseEntity.ok(analysis);
        } else {
            // Si no hay archivo, procesa solo el texto
            String analysis = aiService.analyzeWithGemini(null, text);
            return ResponseEntity.ok(analysis);
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error procesando la solicitud: " + e.getMessage());
    }
}

}