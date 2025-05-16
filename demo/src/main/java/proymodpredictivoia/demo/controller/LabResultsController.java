package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proymodpredictivoia.demo.model.LabResults;
import proymodpredictivoia.demo.repository.LabResultsRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/lab-results")
@CrossOrigin(origins = "http://localhost:3000")
public class LabResultsController {

    @Autowired
    private LabResultsRepository labResultsRepository;

    @PostMapping
    public LabResults saveLabResults(@RequestBody LabResults labResults) {
        return labResultsRepository.save(labResults);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabResults> getLabResults(@PathVariable Long id) {
        return labResultsRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabResults> updateLabResults(@PathVariable Long id, @RequestBody LabResults updated) {
        return labResultsRepository.findById(id)
                .map(existing -> {
                    updated.setId(id);
                    return ResponseEntity.ok(labResultsRepository.save(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabResults(@PathVariable Long id) {
        if (labResultsRepository.existsById(id)) {
            labResultsRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
