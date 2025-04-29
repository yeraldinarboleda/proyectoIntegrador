package proymodpredictivoia.demo.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.regex.*;
import java.util.Map;
import java.util.HashMap;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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


    private static final List<String> FEATURE_KEYS = Arrays.asList(
        "age",
        "gender",
        "chestpain",
        "restingBP",
        "serumcholestrol",
        "fastingbloodsugar",
        "restingrelectro",
        "maxheartrate",
        "exerciseangia",
        "oldpeak",
        "slope",
        "noofmajorvessels"
    );

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

    public String getModelPredictionFromAPI(String jsonPaciente) {
        try {
            WebClient webClient = WebClient.create();
            String response = webClient.post()
                .uri("http://localhost:5000/predict")
                .header("Content-Type", "application/json")
                .bodyValue(jsonPaciente)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    
            return response;
        } catch (Exception e) {
            System.out.println("Error al consultar el modelo: " + e.getMessage());
            return "{\"error\": \"No se pudo obtener la predicción del modelo.\"}";
        }
    }
    

    
    /**
     * Detecta en el texto los campos que necesita el modelo
     * y los convierte en un Map listo para pasar como JSON a /predict
     */
    private Map<String,Object> parsePatientData(String text) {
        Map<String,Object> data = new HashMap<>();

        Matcher m;
        // 1) Edad
        m = Pattern.compile("(?i)\\bEdad[:\\s]*(\\d{1,3})\\b").matcher(text);
        if (m.find()) data.put("age", Integer.parseInt(m.group(1)));

        // 2) Género (0=Femenino, 1=Masculino)
        m = Pattern.compile("(?i)G[eé]nero[:\\s]*(Masculino|Femenino)").matcher(text);
        if (m.find()) {
            String g = m.group(1).toLowerCase();
            data.put("gender", g.startsWith("m") ? 1 : 0);
        }

        // 3) Tipo de dolor torácico / chestpain (0–3)
        m = Pattern.compile("(?i)(?:Chest pain type|Tipo de dolor tor[aá]cico)[:\\s]*(\\d)").matcher(text);
        if (m.find()) data.put("chestpain", Integer.parseInt(m.group(1)));

        // 4) Presión arterial de reposo (sistólica) restingBP
        m = Pattern.compile("(?i)Presi[oó]n arterial[:\\s]*(\\d{2,3})/(\\d{2,3})").matcher(text);
        if (m.find()) data.put("restingBP", Integer.parseInt(m.group(1)));

        // 5) Colesterol sérico total serumcholestrol
        m = Pattern.compile("(?i)(?:Colesterol total|Serum cholesterol)[:\\s]*(\\d{2,3})").matcher(text);
        if (m.find()) data.put("serumcholestrol", Integer.parseInt(m.group(1)));

        // 6) Azúcar en ayunas fastingbloodsugar (0/1)
        m = Pattern.compile("(?i)(?:Az[uú]car en sangre en ayunas|Fasting blood sugar)[:\\s]*(\\d)").matcher(text);
        if (m.find()) data.put("fastingbloodsugar", Integer.parseInt(m.group(1)));

        // 7) Resultados ECG en reposo restingrelectro (0–2)
        m = Pattern.compile("(?i)(?:Resting electrocardiogram.*?results|Electrocardiograma en reposo)[:\\s]*(\\d)").matcher(text);
        if (m.find()) data.put("restingrelectro", Integer.parseInt(m.group(1)));

        // 8) FC máxima maxheartrate
        m = Pattern.compile("(?i)(?:Maximum heart rate achieved|Frecuencia card[ií]aca m[aá]xima)[:\\s]*(\\d{2,3})").matcher(text);
        if (m.find()) data.put("maxheartrate", Integer.parseInt(m.group(1)));

        // 9) Angina inducida por ejercicio exerciseangia (0=No,1=Sí)
        m = Pattern.compile("(?i)(?:Exercise induced angina|Angina inducida por el ejercicio)[:\\s]*(S[ií]|No)").matcher(text);
        if (m.find()) data.put("exerciseangia", m.group(1).toLowerCase().startsWith("s") ? 1 : 0);

        // 10) Oldpeak (depresión ST) oldpeak (float)
        m = Pattern.compile("(?i)(?:Oldpeak|ST)[:\\s]*(\\d+\\.?\\d*)").matcher(text);
        if (m.find()) data.put("oldpeak", Double.parseDouble(m.group(1)));

        // 11) Slope del segmento ST (1–3)
        m = Pattern.compile("(?i)(?:Slope of the peak exercise ST segment|Slope)[:\\s]*(\\d)").matcher(text);
        if (m.find()) data.put("slope", Integer.parseInt(m.group(1)));

        // 12) Número de vasos principales noofmajorvessels (0–3)
        m = Pattern.compile("(?i)(?:Number of major vessels|Número de vasos principales)[:\\s]*(\\d)").matcher(text);
        if (m.find()) data.put("noofmajorvessels", Integer.parseInt(m.group(1)));

        // --- Rellenar con 0 cualquiera que no se haya detectado
        for (String key : FEATURE_KEYS) {
            data.putIfAbsent(key, 0);
        }

        return data;
    }

    private final WebClient predictClient = WebClient.create("http://localhost:5000");
    private PredictResponse callPredictiveModel(Map<String,Object> patientData) {
        // --- imprime el JSON que vas a enviar
        System.out.println("DEBUG: enviando a /predict → " + patientData);
    
        try {
          return predictClient.post()
              .uri("/predict")
              .bodyValue(patientData)
              .retrieve()
              .bodyToMono(PredictResponse.class)
              .block();
        } catch (WebClientResponseException e) {
          // imprime el cuerpo de la respuesta de error
        System.err.println("ERROR 400 from /predict: " + e.getResponseBodyAsString());
        throw e;
        }
    }
    

    public String analyzeWithGeminiFullContext(
        String ocrText,
        String imageDesc,
        String patientDataText
    ) throws IOException {
    
      // 1) Parsear los datos útiles
        Map<String,Object> patientData = parsePatientData(patientDataText);
    
      // 2) Llamar al RF micro-servicio
        PredictResponse pr = callPredictiveModel(patientData);
    
      // 3) Construir prompt
        StringBuilder prompt = new StringBuilder();
            prompt.append("Eres un cardiólogo experto el cual basa su respuesta en un modelo predictivo de la base de datos de datos de pacientes, y debes analizar la siguiente información para dar un diagnostico final:\n\n")
            .append("Datos extraídos:\n")
            .append(ocrText).append("\n\n")
            .append("Descripción de imagen:\n")
            .append(imageDesc).append("\n\n")
            .append("Datos del paciente (JSON):\n")
            .append(patientData).append("\n\n")
            .append("Predicción modelo:\n")
            .append(String.format("Probabilidad de enfermedad cardiovascular: %.2f%%\n", pr.probability*100))
            .append("Top características:\n");
      for (List<Object> feat : pr.top_features) {
        prompt.append("- ").append(feat.get(0)).append(": ").append(feat.get(1)).append("\n");
      }
      prompt.append("\nCon base en toda esta información, como cardiólogo experto ...");
    
      // 4) Enviar a Gemini
      String escaped = prompt.toString().replace("\"","\\\"");
      String body = String.format(
        "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
        escaped
      );
    
      String geminiResp = WebClient.create()
          .post()
          .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key="+geminiApiKey)
          .header("Content-Type","application/json")
          .bodyValue(body)
          .retrieve()
          .bodyToMono(String.class)
          .block();
    
      // parsea y devuelve solo el texto
      JsonNode root = new ObjectMapper().readTree(geminiResp);
      return root.at("/candidates/0/content/parts/0/text").asText();
    }
    
    
}