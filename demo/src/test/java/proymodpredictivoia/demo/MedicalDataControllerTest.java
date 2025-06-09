package proymodpredictivoia.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proymodpredictivoia.demo.controller.MedicalDataController;
import proymodpredictivoia.demo.model.MedicalData;
import proymodpredictivoia.demo.repository.MedicalDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MedicalDataControllerTest {

    @Mock
    private MedicalDataRepository repository;

    @InjectMocks
    private MedicalDataController controller;

    private MedicalData sampleData;

    @BeforeEach
    void setUp() {
        sampleData = createValidMedicalData();
    }

    private MedicalData createValidMedicalData() {
        MedicalData data = new MedicalData();
        data.setId(1L);
        data.setWaistCircumference(85.0);
        data.setSystolicPressure(120);
        data.setDiastolicPressure(80);
        data.setHeartRate(72);
        data.setWeight(70.0);
        data.setHeight(1.75);
        data.setBmi(22.9);
        data.setRestingBP(110);
        data.setSerumCholesterol(180);
        data.setMaxHeartRate(150);
        data.setFastingBloodSugar(90);
        data.setOldpeak(1.2);
        data.setSlope(2);
        data.setNoOfMajorVessels(0);
        return data;
    }

    @Test
    void testSaveMedicalData() {
        when(repository.save(sampleData)).thenReturn(sampleData);
        MedicalData result = controller.saveMedicalData(sampleData);
        assertEquals(sampleData, result);
    }

    @Test
    void testGetAllMedicalData() {
        when(repository.findAll()).thenReturn(Arrays.asList(sampleData));
        List<MedicalData> result = controller.getAllMedicalData();
        assertEquals(1, result.size());
        assertEquals(sampleData, result.get(0));
    }

    @Test
    void testGetMedicalDataById_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleData));
        ResponseEntity<MedicalData> response = controller.getMedicalDataById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleData, response.getBody());
    }

    @Test
    void testGetMedicalDataById_NotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        ResponseEntity<MedicalData> response = controller.getMedicalDataById(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateMedicalData_Found() {
        MedicalData updated = createValidMedicalData();
        updated.setWaistCircumference(90.0);  // cambio para validar

        when(repository.findById(1L)).thenReturn(Optional.of(sampleData));
        ResponseEntity<?> response = controller.updateMedicalData(1L, updated);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(repository).save(any(MedicalData.class));
    }

    @Test
    void testUpdateMedicalData_NotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = controller.updateMedicalData(2L, createValidMedicalData());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteMedicalData_Found() {
        when(repository.existsById(1L)).thenReturn(true);
        ResponseEntity<?> response = controller.deleteMedicalData(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteMedicalData_NotFound() {
        when(repository.existsById(2L)).thenReturn(false);
        ResponseEntity<?> response = controller.deleteMedicalData(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}