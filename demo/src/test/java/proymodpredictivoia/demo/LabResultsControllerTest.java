package proymodpredictivoia.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import proymodpredictivoia.demo.controller.LabResultsController;
import proymodpredictivoia.demo.model.LabResults;
import proymodpredictivoia.demo.repository.LabResultsRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LabResultsController.class)
public class LabResultsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LabResultsRepository labResultsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private LabResults getSampleLabResults() {
        LabResults lab = new LabResults();
        lab.setId(1L);
        lab.setHemoglobinA1c(5.4);
        lab.setFastingGlucose(90.0);
        lab.setTotalCholesterol(180.0);
        lab.setHdl(55.0);
        lab.setLdl(100.0);
        lab.setTriglycerides(120.0);
        lab.setCreatinine(1.1);
        return lab;
    }

    @Test
    public void testSaveLabResults() throws Exception {
        LabResults labResults = getSampleLabResults();

        when(labResultsRepository.save(any(LabResults.class))).thenReturn(labResults);

        mockMvc.perform(post("/api/lab-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labResults)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(labResults)));
    }

    @Test
    public void testGetLabResultsById() throws Exception {
        LabResults labResults = getSampleLabResults();

        when(labResultsRepository.findById(1L)).thenReturn(Optional.of(labResults));

        mockMvc.perform(get("/api/lab-results/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(labResults)));
    }

    @Test
    public void testUpdateLabResults() throws Exception {
        LabResults existing = getSampleLabResults();
        LabResults updated = getSampleLabResults();
        updated.setHemoglobinA1c(6.1);

        when(labResultsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(labResultsRepository.save(any(LabResults.class))).thenReturn(updated);

        mockMvc.perform(put("/api/lab-results/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
    }

    @Test
    public void testDeleteLabResults() throws Exception {
        when(labResultsRepository.existsById(1L)).thenReturn(true);
        doNothing().when(labResultsRepository).deleteById(1L);

        mockMvc.perform(delete("/api/lab-results/1"))
                .andExpect(status().isNoContent());
    }
}
