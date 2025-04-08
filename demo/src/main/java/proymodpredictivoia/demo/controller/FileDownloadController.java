package proymodpredictivoia.demo.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import proymodpredictivoia.demo.ai.AIService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/ai")
public class FileDownloadController {

    @Autowired
    private AIService aiService;

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam("format") String format,
            @RequestParam("content") String content,
            @RequestParam("filename") String filename) throws Exception {

        // ✅ Decodifica el contenido para evitar problemas con caracteres especiales
        String decodedContent = URLDecoder.decode(content, StandardCharsets.UTF_8.toString());
        String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString());

        String outputPath = "ruta/al/directorio/" + decodedFilename + "." + format;

        switch (format.toLowerCase()) {
            case "pdf":
                aiService.generatePDF(decodedContent, outputPath);
                break;
            case "csv":
                aiService.generateCSV(decodedContent, outputPath);
                break;
            case "xlsx":
                aiService.generateExcel(decodedContent, outputPath);
                break;
            default:
                return ResponseEntity.badRequest().body(null);
        }

        File file = new File(outputPath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // ✅ Cambia el Content-Type para Excel
        MediaType mediaType = format.equals("xlsx") ?
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(mediaType)
                .contentLength(file.length())
                .body(resource);
    }
}
