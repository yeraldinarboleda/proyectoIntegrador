package proymodpredictivoia.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
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
            @RequestParam(value = "electroFiles", required = false) MultipartFile[] electroFiles,
            @RequestParam(value = "ecoFiles", required = false) MultipartFile[] ecoFiles
    ) throws IOException {
        System.out.println("→ Endpoint /upload llamado");
        System.out.println("Archivos electro: " + (electroFiles != null ? electroFiles.length : "null"));
        System.out.println("Archivos eco: " + (ecoFiles != null ? ecoFiles.length : "null"));

        String electroPaths = (electroFiles != null && electroFiles.length > 0)
                ? saveFiles(electroFiles, "electros")
                : "";

        String ecoPaths = (ecoFiles != null && ecoFiles.length > 0)
                ? saveFiles(ecoFiles, "ecos")
                : "";

        CardioResults result = new CardioResults();
        result.setElectrocardiogram(electroPaths);
        result.setEchocardiogram(ecoPaths);

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
}
