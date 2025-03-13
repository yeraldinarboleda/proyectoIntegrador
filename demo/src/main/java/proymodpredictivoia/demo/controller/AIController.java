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

    @Autowired
    private AIService aiService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateContent(@RequestParam("text") String text, @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        // Permitir que "files" sea opcional
        String result = aiService.generateContent(text, files);
        return ResponseEntity.ok(result);
    }
    
}