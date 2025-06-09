package proymodpredictivoia.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import proymodpredictivoia.demo.controller.CardioResultsController;
import proymodpredictivoia.demo.model.CardioResults;
import proymodpredictivoia.demo.repository.CardioResultsRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CardioResultsController.class)
public class CardioResultsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardioResultsRepository cardioResultsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CardioResults getSampleCardioResults() {
        CardioResults cr = new CardioResults();
        cr.setId(1L);
        cr.setElectrocardiogram("uploads/electros/sample1.csv");
        cr.setEchocardiogram("uploads/ecos/sample2.csv");
        cr.setChestPainType(1);
        cr.setRestingECG(0);
        cr.setExerciseAngina(1);
        return cr;
    }

    @Test
    public void testGetAllCardioResults() throws Exception {
        when(cardioResultsRepository.findAll()).thenReturn(Arrays.asList(getSampleCardioResults()));

        mockMvc.perform(get("/api/cardio-results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetCardioResultById() throws Exception {
        when(cardioResultsRepository.findById(1L)).thenReturn(Optional.of(getSampleCardioResults()));

        mockMvc.perform(get("/api/cardio-results/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testUpdateCardioResult() throws Exception {
        CardioResults existing = getSampleCardioResults();
        CardioResults updated = getSampleCardioResults();
        updated.setChestPainType(1);

        when(cardioResultsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cardioResultsRepository.save(any(CardioResults.class))).thenReturn(updated);

        mockMvc.perform(put("/api/cardio-results/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().string("Actualizado correctamente."));
    }

    @Test
    public void testDeleteCardioResult() throws Exception {
        when(cardioResultsRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cardioResultsRepository).deleteById(1L);

        mockMvc.perform(delete("/api/cardio-results/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Resultado eliminado correctamente."));
    }

    @Test
    public void testUploadCardioResults() throws Exception {
        MockMultipartFile electroFile = new MockMultipartFile("electroFiles", "electro.csv", "text/csv", "data1".getBytes());
        MockMultipartFile ecoFile = new MockMultipartFile("ecoFiles", "eco.csv", "text/csv", "data2".getBytes());

        CardioResults saved = getSampleCardioResults();
        when(cardioResultsRepository.save(any(CardioResults.class))).thenReturn(saved);

        mockMvc.perform(multipart("/api/cardio-results/upload")
                .file(electroFile)
                .file(ecoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
