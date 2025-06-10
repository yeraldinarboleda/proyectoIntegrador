package proymodpredictivoia.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import proymodpredictivoia.demo.model.CardioResults;
import proymodpredictivoia.demo.repository.CardioResultsRepository;

@RestController
@RequestMapping("/api/cardio-results")
@CrossOrigin(origins = "http://localhost:3000") 
public class CardioResultsController {

    @Autowired
    private CardioResultsRepository cardioResultsRepository;

    @PostMapping("/upload")
public CardioResults uploadCardioResults(
        @RequestParam("documentId") String documentId,
        @RequestParam(value = "electrocardiogramFiles", required = false) MultipartFile[] electroFiles,
        @RequestParam(value = "echocardiogramFiles", required = false) MultipartFile[] ecoFiles,
        @RequestParam(value = "chestPainType", required = false) String chestPainType,
        @RequestParam(value = "restingECG", required = false) String restingECG,
        @RequestParam(value = "exerciseAngina", required = false) String exerciseAngina
) throws IOException {
    System.out.println("→ Endpoint /upload llamado");

    String electroPaths = (electroFiles != null && electroFiles.length > 0)
            ? saveFiles(electroFiles, "electros")
            : "";

    String ecoPaths = (ecoFiles != null && ecoFiles.length > 0)
            ? saveFiles(ecoFiles, "ecos")
            : "";

    CardioResults result = new CardioResults();
    result.setDocumentId(documentId);
    result.setElectrocardiogram(electroPaths);
    result.setEchocardiogram(ecoPaths);

    // Convertir Strings a Integer si no son nulos ni vacíos
    if (chestPainType != null && !chestPainType.isEmpty())
        result.setChestPainType(Integer.parseInt(chestPainType));
    if (restingECG != null && !restingECG.isEmpty())
        result.setRestingECG(Integer.parseInt(restingECG));
    if (exerciseAngina != null && !exerciseAngina.isEmpty())
        result.setExerciseAngina(Integer.parseInt(exerciseAngina));

    return cardioResultsRepository.save(result);
}


    private String saveFiles(MultipartFile[] files, String folder) throws IOException {
        StringBuilder savedPaths = new StringBuilder();
        String uploadDir = "uploads/" + folder;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        for (MultipartFile file : files) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);
            Files.write(path, file.getBytes());

            if (savedPaths.length() > 0) savedPaths.append(",");
            savedPaths.append(path.toString());
        }

        return savedPaths.toString();
    }

    // ✅ READ - Obtener todos
    @GetMapping
    public List<CardioResults> getAllCardioResults() {
        return cardioResultsRepository.findAll();
    }

    // ✅ READ - Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<CardioResults> getCardioResultById(@PathVariable Long id) {
        Optional<CardioResults> result = cardioResultsRepository.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ UPDATE - Actualizar resultado por ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCardioResult(
            @PathVariable Long id,
            @RequestBody CardioResults updatedResult
    ) {
        Optional<CardioResults> optional = cardioResultsRepository.findById(id);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        CardioResults result = optional.get();
        
        result.setElectrocardiogram(updatedResult.getElectrocardiogram());
        result.setEchocardiogram(updatedResult.getEchocardiogram());
        result.setChestPainType(updatedResult.getChestPainType());
        result.setRestingECG(updatedResult.getRestingECG());
        result.setExerciseAngina(updatedResult.getExerciseAngina());

        cardioResultsRepository.save(result);
        return ResponseEntity.ok("Actualizado correctamente.");
    }

    // ✅ DELETE - Eliminar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCardioResult(@PathVariable Long id) {
        if (!cardioResultsRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cardioResultsRepository.deleteById(id);
        return ResponseEntity.ok("Resultado eliminado correctamente.");
    }

    @GetMapping("/by-document/{documentId}")
    public List<CardioResults> getByDocumentId(@PathVariable String documentId) {
        return cardioResultsRepository.findByDocumentId(documentId);
    }
}