package proymodpredictivoia.demo.controller;

import proymodpredictivoia.demo.ai.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateAIResponse(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        // Generar el contenido basado en texto e imágenes
        String aiResponse = aiService.generateContent(text, files);
        
        // Devolver la respuesta al frontend
        return ResponseEntity.ok(aiResponse);
    }
}

