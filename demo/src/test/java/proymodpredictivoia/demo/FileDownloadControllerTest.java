package proymodpredictivoia.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import proymodpredictivoia.demo.ai.AIService;
import proymodpredictivoia.demo.controller.FileDownloadController;
import org.springframework.http.MediaType;


import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;

@WebMvcTest(FileDownloadController.class)
@AutoConfigureMockMvc(addFilters = false)

class FileDownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIService aiService;

    @Test
    void testDownloadPDF() throws Exception {
        // Arrange
        String format = "pdf";
        String content = "Contenido de prueba para PDF";
        String filename = "testfile";
        String decodedFilename = filename + "." + format;
        String path = "ruta/al/directorio/" + decodedFilename;

        // Simula la creación del archivo
        doNothing().when(aiService).generatePDF(content, path);

        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile(); // crea un archivo vacío

        // Act & Assert
        mockMvc.perform(get("/ai/download")
                .param("format", format)
                .param("content", content)
                .param("filename", filename))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + decodedFilename + "\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));

        file.delete(); // Limpieza
    }
}

