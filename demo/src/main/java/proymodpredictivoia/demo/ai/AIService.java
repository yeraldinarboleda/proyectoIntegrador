package proymodpredictivoia.demo.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.image.BufferedImage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Chunk;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
//import com.opencsv.CSVWriter;

@Service
public class AIService {

    @Value("${API_GEMINI_KEY}")
    private String geminiApiKey;

    public String extractTextFromImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return "Archivo vacío o nulo.";
        }
    
        System.out.println("Procesando archivo: " + file.getOriginalFilename());
        
        String contentType = file.getContentType();
        if ("application/pdf".equals(contentType)) {
            String extractedText = extractTextFromPdf(file);
            return (extractedText != null && !extractedText.isEmpty()) ? extractedText : "No se pudo extraer texto del PDF.";
        } else if (contentType != null && contentType.startsWith("image/")) {
            String extractedText = extractTextFromImageFile(file);
            return (extractedText != null && !extractedText.isEmpty()) ? extractedText : "No se pudo extraer texto de la imagen.";
        } else {
            return "Tipo de archivo no soportado: " + contentType;
        }
    }
    
    

    private String extractTextFromImageFile(MultipartFile imageFile) throws IOException {
        ByteString imgBytes = ByteString.readFrom(imageFile.getInputStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> requests = Collections.singletonList(request);
            AnnotateImageResponse response = vision.batchAnnotateImages(requests).getResponses(0);

            if (response.hasError()) {
                throw new RuntimeException("Error en la API de Vision: " + response.getError().getMessage());
            }

            return response.getTextAnnotationsList().isEmpty() ? null : response.getTextAnnotationsList().get(0).getDescription();
        }
    }

    private String extractTextFromPdf(MultipartFile pdfFile) throws IOException {
        // Usa la conversión de páginas a imágenes
        return extractTextFromPdfPages(pdfFile);
    }
    

    private String extractTextFromPdfPages(MultipartFile pdfFile) throws IOException {
        // Cargar el PDF desde el InputStream del MultipartFile
        PDDocument document = PDDocument.load(pdfFile.getInputStream());
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder extractedText = new StringBuilder();

        // Iterar sobre cada página del PDF
        for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);

            // Convertir la imagen a un arreglo de bytes en formato PNG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            // Crear ByteString a partir de los bytes de la imagen
            ByteString imgBytes = ByteString.copyFrom(imageBytes);
            
            // Construir la imagen para Vision API
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

            // Llamar a la API de Vision para esta página
            try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
                List<AnnotateImageRequest> requests = Collections.singletonList(request);
                AnnotateImageResponse response = vision.batchAnnotateImages(requests).getResponses(0);
                
                if (response.hasError()) {
                    extractedText.append("Error en la página ").append(pageIndex).append(": ")
                                .append(response.getError().getMessage()).append("\n");
                } else {
                    String pageText = response.getTextAnnotationsList().isEmpty() ? "" : response.getTextAnnotationsList().get(0).getDescription();
                    extractedText.append(pageText).append("\n\n");
                }
            }
        }
        document.close();
        return extractedText.toString();
    }

    public String describeImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        ByteString imgBytes = ByteString.readFrom(imageFile.getInputStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).setMaxResults(10).build(); // Obtener hasta 10 etiquetas
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

        try {
            System.out.println("Intentando crear ImageAnnotatorClient...");
            ImageAnnotatorClient vision = ImageAnnotatorClient.create();
            System.out.println("ImageAnnotatorClient creado exitosamente.");
            //...
        } catch (IOException e) {
            System.err.println("Error al crear ImageAnnotatorClient: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear ImageAnnotatorClient: " + e.getMessage());
        }

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> requests = Collections.singletonList(request);
            AnnotateImageResponse response = vision.batchAnnotateImages(requests).getResponses(0);

            if (response.hasError()) {
                throw new RuntimeException("Error en la API de Vision: " + response.getError().getMessage());
            }

            StringBuilder description = new StringBuilder();
            for (EntityAnnotation label : response.getLabelAnnotationsList()) {
                description.append(label.getDescription()).append(", ");
            }
            return description.length() > 0 ? description.substring(0, description.length() - 2) : "No se encontraron etiquetas.";
        }
    }

    
    public void generatePDF(String content, String outputPath) throws Exception {
        // Crea el directorio de salida si no existe
        File outputFile = new File(outputPath);
        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();
        
        // Reemplaza los <br> por saltos de línea
        String formattedContent = content.replace("<br>", "\n").replace("<br><br>", "\n\n");
        
        // Define dos fuentes: una normal y otra en negrita
        Font normalFont = new Font(FontFamily.HELVETICA, 12, Font.NORMAL);
        Font boldFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
        
        // Utiliza expresión regular para encontrar segmentos en negrita
        // El patrón busca "cualquier texto" seguido de <b> "texto en negrita" </b>
        Pattern pattern = Pattern.compile("(?s)(.*?)<b>(.*?)</b>");
        Matcher matcher = pattern.matcher(formattedContent);
        
        Paragraph paragraph = new Paragraph();
        int lastEnd = 0;
        while(matcher.find()){
            // Agrega el texto anterior a la etiqueta <b> usando la fuente normal
            String textBefore = matcher.group(1);
            if (!textBefore.isEmpty()) {
                paragraph.add(new Chunk(textBefore, normalFont));
            }
            
            // Agrega el texto que estaba entre <b> y </b> usando la fuente en negrita
            String boldText = matcher.group(2);
            paragraph.add(new Chunk(boldText, boldFont));
            
            lastEnd = matcher.end();
        }
        
        // Agrega cualquier texto restante después del último </b>
        if(lastEnd < formattedContent.length()){
            String remaining = formattedContent.substring(lastEnd);
            paragraph.add(new Chunk(remaining, normalFont));
        }
        
        document.add(paragraph);
        document.close();
    }
    
    


    public void generateCSV(String content, String outputPath) throws IOException {
        try (BufferedWriter csvWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputPath), "UTF-8"))) {
            // Encabezado del CSV
            csvWriter.write("\"Sección\",\"Contenido\"\n");
    
            // Reemplazar <br> por saltos de línea
            String formattedContent = content.replace("<br>", "\n").replace("<br><br>", "\n\n");
            // Opcional: eliminar etiquetas HTML para que no aparezcan en el CSV final
            String plainContent = formattedContent.replaceAll("<[^>]+>", "");
    
            // Escapar las comillas dobles del contenido (ya que en CSV se duplican)
            String escapedContent = plainContent.replace("\"", "\"\"");
    
            csvWriter.write("\"Resultado de la IA\",\"" + escapedContent + "\"\n");
        }
    }
    

    
    public void generateExcel(String content, String outputPath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Resultado IA");

        // Configurar el estilo de la celda para que se ajusten los saltos de línea
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        // Reemplazar <br> por saltos de línea y eliminar etiquetas HTML
        String formattedContent = content.replace("<br>", "\n")
                                        .replace("<br><br>", "\n\n");
        // Eliminar etiquetas HTML (por ejemplo, <b> y </b>)
        formattedContent = formattedContent.replaceAll("<[^>]+>", "");

        // Crear fila de encabezado
        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Resultado");

        // Crear fila de datos
        Row dataRow = sheet.createRow(1);
        Cell contentCell = dataRow.createCell(0);
        contentCell.setCellValue(formattedContent);
        contentCell.setCellStyle(wrapStyle);

        // Ajustar el alto de la fila para que se muestre todo el contenido
        dataRow.setHeight((short) -1);

        // Autoajustar el ancho de la columna
        sheet.autoSizeColumn(0);

        // Guardar el archivo de Excel
        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    public String analyzeWithGemini(String extractedText, String imageDescription) throws IOException {
        WebClient webClient = WebClient.create();

        String prompt = "Eres un cardiologo experto el cual te encargas de analizar el siguiente texto extraido de una imagen y" 
        +"su descripcion, y dar una respuesta basada en el contenido de la imagen, asi como dar un diagnotico final,"+ 
        "tambien puedes responder preguntas sin necesidad de una imagen, solo con el texto, y dar un diagnostico final,"+ 
        "y dar una respuesta basada en el contenido del texto, Ademas tienes que decir que pasaria si no sigue las recomendaciones y que pasaria si sigue las recomendaciones.: " +
        "Texto extraido: " + (extractedText != null ? extractedText : "No se encontró texto.") +
        " Descripcion de la imagen: " + (imageDescription != null ? imageDescription : "No se encontró descripción.");

        String escapedPrompt = prompt.replace("\"", "\\\"");
        String requestBody = String.format("{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}", escapedPrompt);

        System.out.println("Request Body: " + requestBody);

        try {
            String response = webClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=" + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Raw Gemini Response: " + response);

            if (response != null && !response.isEmpty()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response);
                    JsonNode textNode = jsonNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");

                    if (textNode.isTextual()) {
                        return textNode.asText();
                    } else {
                        System.out.println("No text content found in Gemini response.");
                        return "No response from Gemini (text content not found).";
                    }

                } catch (Exception e) {
                    System.out.println("Error parsing Gemini response: " + e.getMessage());
                    return "Error parsing Gemini response.";
                }
            } else {
                System.out.println("Empty response from Gemini.");
                return "No response from Gemini (empty response).";
            }

        } catch (WebClientResponseException e) {
            System.out.println("Error en la solicitud a Gemini: " + e.getMessage());
            System.out.println("Response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error en la API Gemini: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado en la solicitud a Gemini: " + e.getMessage(), e);
        }
    }
}