package proymodpredictivoia.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import proymodpredictivoia.demo.controller.PersonalDataController;
import proymodpredictivoia.demo.model.PersonalData;
import proymodpredictivoia.demo.repository.PersonalDataRepository;

@ExtendWith(MockitoExtension.class)
public class PersonalDataControllerTest {

    @Mock
    private PersonalDataRepository personalDataRepository;

    @InjectMocks
    private PersonalDataController personalDataController;

    private PersonalData sample;

    @BeforeEach
    void setUp() {
        sample = new PersonalData();
        sample.setDocumentId("12345678");
        sample.setDocumentType("DNI");
        sample.setFirstName("Juan");
        sample.setLastName("Pérez");
        sample.setBirthDate(LocalDate.of(1990, 5, 20));
        sample.setGender("masculino");
        sample.setAddress("Calle Falsa 123");
        sample.setContact("987654321");
        sample.setGmail("juan.perez@gmail.com");
    }

    @Test
    void testSavePersonalData() {
        when(personalDataRepository.save(sample)).thenReturn(sample);

        PersonalData saved = personalDataController.savePersonalData(sample);
        assertEquals(sample, saved);
    }

    @Test
    void testGetByDocumentId_Found() {
        when(personalDataRepository.findById("12345678")).thenReturn(Optional.of(sample));

        ResponseEntity<PersonalData> response = personalDataController.getByDocumentId("12345678");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sample, response.getBody());
    }

    @Test
    void testGetByDocumentId_NotFound() {
        when(personalDataRepository.findById("00000000")).thenReturn(Optional.empty());

        ResponseEntity<PersonalData> response = personalDataController.getByDocumentId("00000000");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetAllPersonalData() {
        List<PersonalData> dataList = Arrays.asList(sample);
        when(personalDataRepository.findAll()).thenReturn(dataList);

        List<PersonalData> result = personalDataController.getAllPersonalData();
        assertEquals(1, result.size());
        assertEquals(sample, result.get(0));
    }

    @Test
    void testUpdatePersonalData_Found() {
        PersonalData updated = new PersonalData();
        updated.setDocumentType("DNI");
        updated.setFirstName("Carlos");
        updated.setLastName("Gómez");
        updated.setBirthDate(LocalDate.of(1985, 3, 15));
        updated.setGender("masculino");
        updated.setAddress("Calle Nueva 456");
        updated.setContact("912345678");
        updated.setGmail("carlos.gomez@gmail.com");

        when(personalDataRepository.findById("12345678")).thenReturn(Optional.of(sample));

        ResponseEntity<?> response = personalDataController.updatePersonalData("12345678", updated);
        assertEquals(200, response.getStatusCodeValue());
        verify(personalDataRepository).save(any(PersonalData.class));
    }

    @Test
    void testUpdatePersonalData_NotFound() {
        when(personalDataRepository.findById("99999999")).thenReturn(Optional.empty());

        ResponseEntity<?> response = personalDataController.updatePersonalData("99999999", new PersonalData());
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeletePersonalData_Found() {
        when(personalDataRepository.existsById("12345678")).thenReturn(true);

        ResponseEntity<?> response = personalDataController.deletePersonalData("12345678");
        assertEquals(200, response.getStatusCodeValue());
        verify(personalDataRepository).deleteById("12345678");
    }

    @Test
    void testDeletePersonalData_NotFound() {
        when(personalDataRepository.existsById("99999999")).thenReturn(false);

        ResponseEntity<?> response = personalDataController.deletePersonalData("99999999");
        assertEquals(404, response.getStatusCodeValue());
    }
}

