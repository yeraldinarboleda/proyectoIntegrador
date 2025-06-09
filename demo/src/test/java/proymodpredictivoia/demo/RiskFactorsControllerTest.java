package proymodpredictivoia.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import proymodpredictivoia.demo.controller.RiskFactorsController;
import proymodpredictivoia.demo.model.RiskFactors;
import proymodpredictivoia.demo.repository.RiskFactorsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class RiskFactorsControllerTest {

    @Mock
    private RiskFactorsRepository riskFactorsRepository;

    @InjectMocks
    private RiskFactorsController riskFactorsController;

    private RiskFactors sample;

    @BeforeEach
    void setUp() {
        sample = new RiskFactors();
        sample.setId(1L);
        sample.setSmoking(true);
        sample.setDrugUse(false);
        sample.setAlcoholConsumption(true);
        sample.setPhysicalActivity("Moderada");
        sample.setDiet("Balanceada");
        sample.setDiabetes(true);
        sample.setDiabetesType("Tipo 2");
        sample.setHighCholesterol(false);
        sample.setHypertension(true);
        sample.setMedicationUse(true);
        sample.setCardiovascularDiseases(true);
        sample.setCardiovascularDiseaseType("Arritmia");
        sample.setOtherCardiovascularDiseases("Ninguna");
    }

    @Test
    void testSaveRiskFactors() {
        when(riskFactorsRepository.save(sample)).thenReturn(sample);

        RiskFactors saved = riskFactorsController.saveRiskFactors(sample);
        assertEquals(sample, saved);
    }

    @Test
    void testGetAllRiskFactors() {
        List<RiskFactors> expectedList = Arrays.asList(sample);
        when(riskFactorsRepository.findAll()).thenReturn(expectedList);

        List<RiskFactors> actualList = riskFactorsController.getAllRiskFactors();
        assertEquals(1, actualList.size());
        assertEquals(sample, actualList.get(0));
    }

    @Test
    void testGetRiskFactorsById_Found() {
        when(riskFactorsRepository.findById(1L)).thenReturn(Optional.of(sample));

        ResponseEntity<RiskFactors> response = riskFactorsController.getRiskFactorsById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sample, response.getBody());
    }

    @Test
    void testGetRiskFactorsById_NotFound() {
        when(riskFactorsRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<RiskFactors> response = riskFactorsController.getRiskFactorsById(2L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateRiskFactors_Found() {
        RiskFactors updated = new RiskFactors();
        updated.setSmoking(false);
        updated.setDrugUse(true);
        updated.setAlcoholConsumption(false);
        updated.setPhysicalActivity("Alta");
        updated.setDiet("Vegetariana");
        updated.setDiabetes(false);
        updated.setDiabetesType("Tipo 1");
        updated.setHighCholesterol(true);
        updated.setHypertension(false);
        updated.setMedicationUse(false);
        updated.setCardiovascularDiseases(false);
        updated.setCardiovascularDiseaseType("N/A");
        updated.setOtherCardiovascularDiseases("Ninguna");

        when(riskFactorsRepository.findById(1L)).thenReturn(Optional.of(sample));

        ResponseEntity<?> response = riskFactorsController.updateRiskFactors(1L, updated);
        assertEquals(200, response.getStatusCodeValue());
        verify(riskFactorsRepository).save(any(RiskFactors.class));
    }

    @Test
    void testUpdateRiskFactors_NotFound() {
        when(riskFactorsRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = riskFactorsController.updateRiskFactors(2L, new RiskFactors());
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteRiskFactors_Found() {
        when(riskFactorsRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<?> response = riskFactorsController.deleteRiskFactors(1L);
        assertEquals(200, response.getStatusCodeValue());
        verify(riskFactorsRepository).deleteById(1L);
    }

    @Test
    void testDeleteRiskFactors_NotFound() {
        when(riskFactorsRepository.existsById(99L)).thenReturn(false);

        ResponseEntity<?> response = riskFactorsController.deleteRiskFactors(99L);
        assertEquals(404, response.getStatusCodeValue());
    }
}

