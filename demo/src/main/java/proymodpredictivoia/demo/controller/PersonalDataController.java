package proymodpredictivoia.demo.controller;

import proymodpredictivoia.demo.model.PersonalData;
import proymodpredictivoia.demo.repository.PersonalDataRepository;
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
}
