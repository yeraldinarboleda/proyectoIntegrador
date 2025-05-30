package proymodpredictivoia.demo.controller;

import proymodpredictivoia.demo.model.PersonalData;
import proymodpredictivoia.demo.repository.PersonalDataRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personal-data")
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde React
public class PersonalDataController {

    @Autowired
    private PersonalDataRepository personalDataRepository;

    @PostMapping
    public PersonalData savePersonalData(@RequestBody PersonalData personalData) {
        return personalDataRepository.save(personalData);
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<PersonalData> getByDocumentId(@PathVariable String documentId) {
        return personalDataRepository.findById(documentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ READ - Todos
    @GetMapping
    public List<PersonalData> getAllPersonalData() {
        return personalDataRepository.findAll();
    }

    // ✅ UPDATE
    @PutMapping("/{documentId}")
    public ResponseEntity<?> updatePersonalData(@PathVariable String documentId, @RequestBody PersonalData updatedData) {
        Optional<PersonalData> optional = personalDataRepository.findById(documentId);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PersonalData existing = optional.get();
        existing.setDocumentType(updatedData.getDocumentType());
        existing.setFirstName(updatedData.getFirstName());
        existing.setLastName(updatedData.getLastName());
        existing.setBirthDate(updatedData.getBirthDate());
        existing.setGender(updatedData.getGender());
        existing.setAddress(updatedData.getAddress());
        existing.setContact(updatedData.getContact());
        existing.setGmail(updatedData.getGmail());

        personalDataRepository.save(existing);
        return ResponseEntity.ok("Datos personales actualizados correctamente.");
    }

    // ✅ DELETE
    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deletePersonalData(@PathVariable String documentId) {
        if (!personalDataRepository.existsById(documentId)) {
            return ResponseEntity.notFound().build();
        }

        personalDataRepository.deleteById(documentId);
        return ResponseEntity.ok("Datos personales eliminados correctamente.");
    }
}
