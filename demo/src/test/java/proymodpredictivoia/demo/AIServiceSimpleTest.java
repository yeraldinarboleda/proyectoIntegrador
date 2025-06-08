package proymodpredictivoia.demo;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import proymodpredictivoia.demo.ai.AIService;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class AIServiceSimpleTest {

    private MockMultipartFile getPdf(String name) throws Exception {
        File file = new File("src/test/resources/pdfs/" + name);
        return new MockMultipartFile("file", file.getName(), "application/pdf", new FileInputStream(file));
    }

    @Test
    public void testID02_CP_01_textoClaro() throws Exception {
        AIService svc = new AIService();
        var result = svc.extractTextFromImage(getPdf("ID02-CP-01_texto_claro.pdf"));
        assertFalse(result.isEmpty());
    }

    @Test
    public void testID02_CP_02_escaneado() throws Exception {
        AIService svc = new AIService();
        var result = svc.extractTextFromImage(getPdf("ID02-CP-02_escaneado.pdf"));
        assertFalse(result.isEmpty()); // OCR debería extraer texto
    }

    @Test
    public void testID02_CP_03_variasPaginas() throws Exception {
        AIService svc = new AIService();
        var result = svc.extractTextFromImage(getPdf("ID02-CP-03_varias_paginas.pdf"));
        assertTrue(result.contains("Página 1") && result.contains("Página 3"));
    }


    @Test
    public void testID02_CP_06_corrupto() throws Exception {
        AIService svc = new AIService();
        Exception ex = assertThrows(Exception.class, () -> {
            svc.extractTextFromImage(getPdf("ID02-CP-06_corrupto.pdf"));
        });
        System.out.println("Excepción esperada: " + ex.getMessage());
    }

    @Test
    public void testID02_CP_07_borroso() throws Exception {
        AIService svc = new AIService();
        var result = svc.extractTextFromImage(getPdf("ID02-CP-07_borroso.pdf"));
        assertFalse(result.isEmpty()); // Al menos texto parcial
    }

    @Test
    public void testID02_CP_08_especiales() throws Exception {
        AIService svc = new AIService();
        var result = svc.extractTextFromImage(getPdf("ID02-CP-08_especiales.pdf"));
        assertTrue(result.contains("ñ") || result.contains("á") || result.contains("©"));
    }

    @Test
    public void testID02_CP_10_manuscrito() throws Exception {
        AIService svc = new AIService();
        var result = svc.extractTextFromImage(getPdf("ID02-CP-10_manuscrito.pdf"));
        assertFalse(result.isEmpty()); // OCR puede funcionar parcialmente
    }

    @Test
    public void testID02_CP_11_rotado() throws Exception {
        AIService svc = new AIService();
        var result = svc.extractTextFromImage(getPdf("ID02-CP-11_rotado.pdf"));
        assertTrue(result.contains("rotada") || result.length() > 10);
    }


}
