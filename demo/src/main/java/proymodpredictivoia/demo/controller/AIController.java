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
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "text", required = false) String text) {
        try {
            System.out.println("Files received: " + (files != null ? files.length : "null"));
            System.out.println("Text received: " + text);
            
            if ((files == null || files.length == 0) && (text == null || text.isEmpty())) {
                return ResponseEntity.badRequest().body("Se requiere al menos una imagen o un texto.");
            }
            
            StringBuilder combinedText = new StringBuilder();
            StringBuilder fileDescriptions = new StringBuilder();
            
            // Agrega el texto ingresado por el usuario, si existe
            if (text != null && !text.isEmpty()) {
                combinedText.append("Texto ingresado por el usuario:\n").append(text).append("\n\n");
            }
            
            // Procesa todos los archivos y concatena su texto extraído y sus descripciones
            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String extractedText;
                        String imageDescription;
                        try {
                            extractedText = aiService.extractTextFromImage(file);
                        } catch (Exception e) {
                            extractedText = "Error al extraer texto: " + e.getMessage();
                        }
                        try {
                            imageDescription = aiService.describeImage(file);
                        } catch (Exception e) {
                            imageDescription = "Error al generar descripción: " + e.getMessage();
                        }
                        
                        // Si la extracción devuelve null o vacío, asigna un mensaje por defecto
                        if (extractedText == null || extractedText.isEmpty()) {
                            extractedText = "No se pudo extraer texto de este archivo.";
                        }
                        if (imageDescription == null || imageDescription.isEmpty()) {
                            imageDescription = "No se pudo generar una descripción para este archivo.";
                        }
                        
                        combinedText.append("ARCHIVO: ").append(file.getOriginalFilename()).append("\n")
                                    .append("Texto extraído:\n").append(extractedText).append("\n\n");
                        fileDescriptions.append("Descripción de ").append(file.getOriginalFilename()).append(":\n")
                                        .append(imageDescription).append("\n\n");
                    }
                }
            }
            
            if (combinedText.toString().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("No se pudo extraer texto de ningún archivo.");
            }
            
            // Llama a la IA (Gemini) enviando la información combinada de texto y de archivos
            String analysis = aiService.analyzeWithGemini(combinedText.toString(), fileDescriptions.toString());
            return ResponseEntity.ok(analysis);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error procesando la solicitud: " + e.getMessage());
        }
    }
    
    

}