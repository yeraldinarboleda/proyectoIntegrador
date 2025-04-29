package proymodpredictivoia.demo.controller;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proymodpredictivoia.demo.ai.AIService;
import org.springframework.http.HttpStatus;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /** 
     * Recibe: 
     * - archivos (imágenes o PDF), 
     * - texto libre opcional, 
     * - y los datos del paciente como JSON (patientData).
     * Devuelve el análisis de Gemini integrado con la predicción del modelo.
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateFromImageTextAndPatient(
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "text", required = false) String text
            //@RequestParam(value = "patientData", required = true) String patientDataJson
    ) {
        try {
            // 1) Validar entrada mínima
            if ((files == null || files.length == 0) && (text == null || text.isBlank())) {
                return ResponseEntity.badRequest().body("Se requiere al menos un archivo o un texto.");
            }

            // 2) Extraer texto y descripciones de cada archivo
            StringBuilder combinedText = new StringBuilder();
            StringBuilder fileDescriptions = new StringBuilder();

            if (text != null && !text.isBlank()) {
                combinedText.append("Texto ingresado por el usuario:\n")
                            .append(text).append("\n\n");
            }

            if (files != null) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String extracted = aiService.extractTextFromImage(file);
                        String desc      = aiService.describeImage(file);

                        if (extracted == null || extracted.isBlank()) {
                            extracted = "No se pudo extraer texto de este archivo.";
                        }
                        if (desc == null || desc.isBlank()) {
                            desc = "No se pudo generar descripción para este archivo.";
                        }

                        combinedText.append("ARCHIVO: ")
                                    .append(file.getOriginalFilename()).append("\n")
                                    .append("Texto extraído:\n")
                                    .append(extracted).append("\n\n");

                        fileDescriptions.append("Descripción de ")
                                        .append(file.getOriginalFilename()).append(":\n")
                                        .append(desc).append("\n\n");
                    }
                }
            }

            // 3) Llamar al servicio que integra Vision AI + Modelo predictivo + Gemini
            String result = aiService.analyzeWithGeminiFullContext(
            combinedText.toString(),
            fileDescriptions.toString(),
            (text!=null? text: "") + "\n" + combinedText.toString()
        );
        return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Error procesando la solicitud: " + e.getMessage());
        }
    }

    
    @GetMapping("/ai/download/excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam String content) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Resultado IA");

            // Crear encabezado
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Resultado");

            // Agregar contenido
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(content);

            // Convertir el archivo a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            byte[] excelBytes = outputStream.toByteArray();

            // Configurar los encabezados HTTP para la descarga
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=resultado.xlsx");
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}