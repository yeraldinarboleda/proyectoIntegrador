package proymodpredictivoia.demo.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

//import com.opencsv.CSVWriter;

@Service
public class AIService {

    private static final List<String> FEATURE_KEYS = List.of(
        "age","restingBP","serumcholestrol","maxheartrate","oldpeak",
        "noofmajorvessels","gender","fastingbloodsugar","exerciseangia",
        "chestpain","restingrelectro","slope"
    );

    // Cliente a tu micro-servicio Flask
    private final WebClient predictClient = WebClient.create("http://localhost:5000");

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
    
    
  /**
   * Sobrecarga para PDF que escribe directamente en el OutputStream dado.
   */
    public void generatePDF(String content, OutputStream os) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, os);
        document.open();
        String formatted = content.replace("<br>", "\n").replace("<br><br>", "\n\n");
        Font normal = new Font(FontFamily.HELVETICA, 12, Font.NORMAL);
        Font bold   = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
        Pattern p = Pattern.compile("(?s)(.*?)<b>(.*?)</b>");
        Matcher m = p.matcher(formatted);
        Paragraph para = new Paragraph();
        int last=0;
        while(m.find()){
        String before = m.group(1);
        if(!before.isEmpty()) para.add(new Chunk(before, normal));
        para.add(new Chunk(m.group(2), bold));
        last = m.end();
        }
        if(last < formatted.length()){
        para.add(new Chunk(formatted.substring(last), normal));
        }
        document.add(para);
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
    
    /**
   * Sobrecarga para CSV que escribe directamente en el OutputStream dado.
   */
    public void generateCSV(String content, OutputStream os) throws IOException {
    try (BufferedWriter csvWriter = new BufferedWriter(
            new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
        csvWriter.write("\"Sección\",\"Contenido\"\n");
        String formatted = content.replace("<br>", "\n").replace("<br><br>", "\n\n");
        String plain     = formatted.replaceAll("<[^>]+>", "");
        String escaped   = plain.replace("\"", "\"\"");
        csvWriter.write("\"Resultado de la IA\",\"" + escaped + "\"\n");
        csvWriter.flush();
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



  /**
   * Sobrecarga para Excel que escribe directamente en el OutputStream dado.
   */
  public void generateExcel(String content, OutputStream os) throws IOException {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Resultado IA");
    CellStyle wrap = workbook.createCellStyle();
    wrap.setWrapText(true);

    String formatted = content.replace("<br>", "\n").replace("<br><br>", "\n\n")
                              .replaceAll("<[^>]+>", "");
    Row hdr = sheet.createRow(0); hdr.createCell(0).setCellValue("Resultado");
    Row row = sheet.createRow(1);
    Cell cell = row.createCell(0);
    cell.setCellValue(formatted);
    cell.setCellStyle(wrap);
    row.setHeight((short)-1);
    sheet.autoSizeColumn(0);

    workbook.write(os);
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
     *

     * Extrae del texto todas las características necesarias para el modelo
     */
    public Map<String,Object> parsePatientData(String text) {
        //text = text.replace("\n", " ").replace("\r", " ").replace("\t", " ");
        System.out.println("TEXTO A PARSEAR: " + text);
        Map<String,Object> data = new HashMap<>();
        Matcher m;

        // 1) Edad
        m = Pattern.compile("(?i)(?:Edad[:\\s]*|Paciente de\\s*|age[:\\s]*)(\\d{1,3})(?:\\s*a[nñ]os|\\s*years)?")
                .matcher(text);

        if (m.find()) data.put("age", Integer.parseInt(m.group(1)));

        // 2) Género
        m = Pattern.compile("(?i)(?:Sexo|G[eé]nero|gender)[:\\s]*(Masculino|Femenino|Male|Female)")
        .matcher(text);
        if (m.find()) {
            data.put("gender", m.group(1).toLowerCase().startsWith("m") ? 1 : 0);
        }


        // 3) Presión arterial sistólica
        m = Pattern.compile("(?i)(?:Presi[oó]n arterial|resting ?BP)[^\\d]*(\\d{2,3})")
        .matcher(text);

        if (m.find()) data.put("restingBP", Integer.parseInt(m.group(1)));

        // 4) Colesterol total
        m = Pattern.compile("(?i)(?:Colesterol(?: sérico total)?|serumcholesterol)[^\\d]*(\\d{2,3})")
        .matcher(text);

        if (m.find()) data.put("serumcholestrol", Integer.parseInt(m.group(1)));

        // 5) Frecuencia cardíaca máxima
        m = Pattern.compile("(?i)(?:Frecuencia cardíaca máxima|ritmo máximo|max(?:imum)?\\s*heartrate|maxHeartRate)[^\\d]*(\\d{2,3})")
        .matcher(text);

        if (m.find()) data.put("maxheartrate", Integer.parseInt(m.group(1)));

        // 6) Oldpeak (depresión ST)
        m = Pattern.compile("(?i)(?:Oldpeak|depresión(?: del segmento ST)?|oldpeak?)[^\\d]*(\\d+\\.?\\d*)")
        .matcher(text);

        if (m.find()) data.put("oldpeak", Double.parseDouble(m.group(1)));

        // 7) Angina inducida por ejercicio
        m = Pattern.compile("(?i)(?:Exercise induced angina|Angina inducida por el ejercicio|exerciseAngina)[:\\s]*(S[ií]|No|Yes|Y|N)")
                .matcher(text);
        if (m.find()) {
            String val = m.group(1).toLowerCase();
            data.put("exerciseangia", (val.startsWith("s") || val.startsWith("y")) ? 1 : 0);
        } else if (text.toLowerCase().matches(".*angina.*(ejercicio|esfuerzo).*")) {
            data.put("exerciseangia", 1);
        }


        // 8) Chest pain type
        m = Pattern.compile("(?i)(?:Chest pain type|dolor tor[aá]cico|chestPainType)[^\\d]*([0-3])")
        .matcher(text);

        if (m.find()) data.put("chestpain", Integer.parseInt(m.group(1)));

        // 9) Resting ECG results
        m = Pattern.compile("(?i)(?:electrocardiograma en reposo|ECG en reposo|Resting electrocardiogram|restingECG|Resting electrocardiogram results)[^\\d]*(?:tipo)?\\s*([0-2])")
        .matcher(text);

        if (m.find()) data.put("restingrelectro", Integer.parseInt(m.group(1)));

        // 10) Slope
        int slopeVal = -1;

        // 10.a) Slope: 2
        Matcher m1 = Pattern.compile("(?i)(?:\\bSlope|slope)\\s*[:：\\-]?\\s*([1-3])\\b").matcher(text);

        if (m1.find()) {
            slopeVal = Integer.parseInt(m1.group(1));
        }

        // 10.b) pendiente plana (código 2) o flat (code 2)
        if (slopeVal < 0) {
            Matcher m2 = Pattern.compile("(?i)(?:plana|flat)\\s*\\(c[oó]digo\\s*([1-3])\\)")
                                .matcher(text);
            if (m2.find()) {
                slopeVal = Integer.parseInt(m2.group(1));
            }
        }

        // 10.c) pendiente … código 2
        if (slopeVal < 0) {
            Matcher m3 = Pattern.compile("(?i)pendiente[\\s\\w]*?c[oó]digo\\s*([1-3])\\b")
                                .matcher(text);
            if (m3.find()) {
                slopeVal = Integer.parseInt(m3.group(1));
            }
        }

        // Finalmente, si encontramos un valor válido, lo guardamos
        if (slopeVal >= 0) {
            System.out.println("DEBUG SLOPE ENCONTRADO: " + slopeVal);
            data.put("slope", slopeVal);
        }

        
        // 11) Número de vasos principales
        m = Pattern.compile("(?i)\\b(\\d|uno|un|dos|tres|1|2|3)\\s+(vaso principal|vasos principales|number of major vessels|major vessels|número de vasos principales|noofmajorvessels|numMajorVessels)")
                .matcher(text);

        if (m.find()) {
            String g = m.group(1).toLowerCase();
            int v = switch (g) {
                case "un", "uno", "1" -> 1; // "un" o "uno" se interpreta como 1
                case "dos", "2"  -> 2;
                case "tres", "3" -> 3;
                default     -> Integer.parseInt(g);
            };
            data.put("noofmajorvessels", v);
        }

        // 12) Azúcar en ayunas (fastingbloodsugar)
        m = Pattern.compile("(?i)(?:Az[uú]car en (?:sangre )?en ayunas|Az[uú]car en ayunas|fasting blood sugar|fastingbloodsugar)\\s*[:\\-]?\\s*([01]|s[ií]|no)")
        .matcher(text);
        if (m.find()) {
            String val = m.group(1).toLowerCase();
            if (val.equals("1") || val.startsWith("s")) {
                data.put("fastingbloodsugar", 1);
            } else {
                data.put("fastingbloodsugar", 0);
            }
        } else if (text.toLowerCase().contains("glucemia en ayunas")) {
            data.put("fastingbloodsugar", 1);
        }


        // Rellenar el resto con 0
        for (String key : FEATURE_KEYS) {
            data.putIfAbsent(key, 0);
        }

        System.out.println("DEBUG: parsePatientData → " + data);
        return data;
    }





    /** 2) Invoca a tu modelo Flask y devuelve la respuesta */
    public PredictResponse callPredictiveModel(Map<String,Object> patientData) {
        System.out.println("DEBUG: enviando a /predict → " + patientData);
        try {
            return predictClient.post()
                .uri("/predict")
                .bodyValue(patientData)
                .retrieve()
                .bodyToMono(PredictResponse.class)
                .block();
        } catch (WebClientResponseException e) {
            System.err.println("ERROR al llamar a /predict: " + e.getResponseBodyAsString());
            throw e;
        }
    }



    /**
     * 3) Construye el prompt uniendo OCR, descripción de imagen,
     *    JSON de paciente + salida del modelo, y lo envía a Gemini.
     */
    public String analyzeWithGeminiFullContext(
            String ocrText,
            String imageDesc,
            String freeText
    ) throws IOException {
        // 1) Parsear datos y llamar al microservicio predictivo
        Map<String,Object> patientData = parsePatientData(freeText + "\n" + ocrText);
        PredictResponse pr       = callPredictiveModel(patientData);

        // 2) Preparar el prompt
        ObjectMapper om = new ObjectMapper();
        String patientJson = om.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(patientData);

        StringBuilder p = new StringBuilder()
        .append("Eres un cardiólogo experto. Usa SOLO los datos clínicos y la predicción del modelo para diagnosticar. No inventes información.\n\n")

        // Contexto
        .append("1) OCR extraído:\n").append(ocrText).append("\n\n")
        .append("2) Descripción de imagen:\n").append(imageDesc).append("\n\n")
        .append("3) Datos del paciente (JSON):\n").append(patientJson).append("\n\n")
        .append("4) Predicción del modelo:\n")
        .append(String.format("   • Probabilidad de enfermedad cardiovascular: %.2f%%\n", pr.getProbability() * 100))
        .append("   • Top características influyentes:\n");
    for (List<Object> feat : pr.getTop_features()) {
        p.append(String.format("     - %s: %s\n", feat.get(0), feat.get(1).toString()));
    }
    p.append("\n---\n")
    // Nueva sección: dashboard
    .append("5) Dashboard del paciente:\n")
    .append("   • Edad: ").append(patientData.get("age")).append(" años\n")
    .append("   • Género: ").append((Integer)patientData.get("gender") == 1 ? "Masculino" : "Femenino").append("\n")
    .append("   • Probabilidad: ").append(pr.getProbability()*100).append("%\n")
    .append("   • Presión arterial sistólica: ").append(patientData.get("restingBP")).append(" mmHg\n")
    .append("   • Colesterol total: ").append(patientData.get("serumcholestrol")).append(" mg/dL\n")
    .append("   • FC máxima: ").append(patientData.get("maxheartrate")).append("\n")
    .append("   • Oldpeak: ").append(patientData.get("oldpeak")).append("\n")
    .append("   • Nº vasos principales: ").append(patientData.get("noofmajorvessels")).append("\n") 
    .append("   • Angina inducida por ejercicio: ").append(((Integer)patientData.get("exerciseangia")==1)?"Sí":"No").append("\n\n");
        for (List<Object> feat : pr.getTop_features()) {
        p.append(String.format("     - %s: %s\n", feat.get(0), feat.get(1).toString()));
    }
    p.append("\n---\n")
    // Visualizaciones
    .append("6) Visualizaciones y simulaciones:\n")
    .append("   a) Gráfica de riesgo actual vs. riesgo medio poblacional.\n")
    .append("   b) Simulación de evolución del riesgo si el paciente:\n")
    .append("      • Mejora dieta (50% reducción de colesterol): proyecta riesgo a 6, 12 y 24 meses.\n")
    .append("      • Aumenta ejercicio (10% incremento en FC máxima): proyecta riesgo a los mismos plazos.\n")
    .append("      • No sigue recomendaciones: proyecta tendencia actual.\n")
    .append("   Para cada escenario, describe:\n")
    .append("      - Tabla con probabilidades estimadas en cada horizonte temporal.\n")
    .append("      - Gráfica de líneas con las tres curvas (paciente vs. medio poblacional).\n\n")

    // Instrucciones finales
    .append("Como cardiólogo, responde:\n")
    .append(" a) Diagnóstico (presencia/ausencia).\n")
    .append(" b) Factores de riesgo.\n")
    .append(" c) Recomendaciones específicas.\n")
    .append(" d) Consecuencias de no seguir recomendaciones.\n")
    .append("e) Incluye el dashboard y las descripciones de las gráficas solicitadas.\n");

    String bodyJson = om.writeValueAsString(
        Map.of("contents", List.of(
            Map.of("parts", List.of(Map.of("text", p.toString())))
        ))
    );


        // 3) Construir JSON para generateContent
        ObjectNode root    = om.createObjectNode();
        ArrayNode contents = root.putArray("contents");
        ObjectNode item    = contents.addObject();
        ArrayNode parts    = item.putArray("parts");
        parts.addObject().put("text", p.toString());
        bodyJson = om.writeValueAsString(root);

        System.out.println("=== GEMINI REQUEST ===\n" + bodyJson);

        // 4) Llamar al mismo endpoint que usaste en Python:
        String raw = WebClient.create()
            .post()
            .uri("https://generativelanguage.googleapis.com/v1beta/models/"
                + "gemini-2.0-flash:generateContent?key=" + geminiApiKey)
            .header("Content-Type", "application/json")
            .bodyValue(bodyJson)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        // 5) Parsear y devolver solo el texto
        JsonNode resp = om.readTree(raw);
        return resp.at("/candidates/0/content/parts/0/text").asText();
    }


    // Al final de AIService.java
public static void main(String[] args) {
    AIService svc = new AIService();
    String ejemplo ="gender: masculino"
        + "age: 68 años\n"
        + "restingBP: 165/95 mmHg\n"
        + "serumcholesterol: 300 mg/dl\n"
        + "maxHeartRate: 110\n"
        + "fastingBloodSugar: 1\n"
        + "chestPainType: 2\n"
        + "restingECG: 1\n"
        + "exerciseAngina: Sí\n"
        + "oldpeak: 4.2\n"
        + "slope: 2\n"
        + "numMajorVessels: 2\n";
    Map<String,Object> datos = svc.parsePatientData(ejemplo);
    System.out.println("Resultado de parsePatientData:\n" + datos);

//         String ocrText = "Paciente: Juan Pérez, Edad: 55, Colesterol Total: 240 mg/dL, Presión Arterial: 145/92 mmHg. ECG muestra ligera hipertrofia ventricular izquierda.";
//         String imageDesc = "Imagen de un electrocardiograma (ECG) con anotaciones de ondas P, QRS y T. No se observan arritmias evidentes pero sí signos de HVI.";
//         String freeText = "El paciente reporta fatiga ocasional y disnea de esfuerzo leve. Niega dolor torácico. Fumador de 10 cigarrillos/día por 20 años.";

//         ocrText="";
//         imageDesc="";
//         freeText="";
//         try {
//             String result = svc.analyzeWithGeminiFullContext(ocrText, imageDesc, freeText);
//             System.out.println("\n--- RESPUESTA DE GEMINI ---");
//             System.out.println(result);
//         } catch (Exception e) {
//             System.err.println("Error al analizar con Gemini: " + e.getMessage());
//         }

// }

/* 
"genero: femenino"
        + "Edad: 68 años\n"
        + "Presión arterial: 165/95 mmHg\n"
        + "Colesterol total: 300 mg/dl\n"
        + "Frecuencia cardíaca máxima: 110\n"
        + "Azúcar en ayunas: 1\n"
        + "Chest pain type: 2\n"
        + "Resting electrocardiogram results: 1\n"
        + "Exercise induced angina: Sí\n"
        + "Oldpeak: 4.2\n"
        + "Slope: 2\n"
        + "Number of major vessels: 2\n";

"gender: femenino"
        + "age: 68 años\n"
        + "restingBP: 165/95 mmHg\n"
        + "serumcholesterol: 300 mg/dl\n"
        + "maxHeartRate: 110\n"
        + "fastingBloodSugar: 1\n"
        + "chestPainType: 2\n"
        + "restingECG: 1\n"
        + "exerciseAngina: Sí\n"
        + "oldpeak: 4.2\n"
        + "slope: 2\n"
        + "numMajorVessels: 2\n";


"    "Paciente de 68 años, sexo femenino, con presión arterial en reposo de 165 mm Hg "
  + "y colesterol sérico total de 300 mg/dL. Su frecuencia cardíaca máxima alcanzada fue de 110 lpm. "
  + "Presenta depresión del segmento ST (“oldpeak”) de 4.2, lo que sugiere isquemia inducida por el ejercicio, "
  + "ya que además tiene angina durante el esfuerzo. El tipo de dolor torácico corresponde al código 2 "
  + "(dolor no anginoso). Los resultados del electrocardiograma en reposo muestran alteraciones de tipo 1 "
  + "(ST–T anormal). La pendiente del segmento ST durante el ejercicio es plana (código 2). "
  + "Se han identificado dos vasos principales afectados. "
  + "Azúcar en sangre en ayunas: 1";

  nombre: maria, edad: 68 años, sexo femenino, oldpeak:4.2, electrocardiograma en reposo alteraciones tipo 1, 
  tiene angina durante el esfuerzo, dolor torácico: 2, presión arterial en reposo de 165, colesterol:300, 
  frecuencia cardíaca máxima alcanzada: 110, La pendiente del segmento ST durante el ejercicio es plana(codigo 2), 
  dos vasos principales afectados, Azúcar en sangre en ayunas: 1

  nombre: maria, edad: 20 años, sexo femenino, oldpeak:0, electrocardiograma en reposo alteraciones tipo 1, 
  tiene angina durante el esfuerzo, dolor torácico: 1, presión arterial en reposo de 100, colesterol:200, 
  frecuencia cardíaca máxima alcanzada: 90, La pendiente del segmento ST durante el ejercicio es codigo 2, 
  dos vasos principales afectados, Azúcar en sangre en ayunas: 0

*/
   
    }
}