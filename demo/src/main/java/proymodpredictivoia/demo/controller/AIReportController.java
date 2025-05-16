package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proymodpredictivoia.demo.model.AIReport;
import proymodpredictivoia.demo.model.PersonalData;
import proymodpredictivoia.demo.repository.AIReportRepository;
import proymodpredictivoia.demo.repository.PersonalDataRepository;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/ai-reports")
public class AIReportController {

    @Autowired
    private AIReportRepository aiReportRepository;

    @Autowired
    private PersonalDataRepository personalDataRepository;

    @PostMapping("/{documentId}")
    public ResponseEntity<?> saveOrUpdateReport(@PathVariable String documentId, @RequestBody String reportData) {
        Optional<PersonalData> personalDataOpt = personalDataRepository.findById(documentId);

        if (personalDataOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PersonalData personalData = personalDataOpt.get();
        Optional<AIReport> existingReport = aiReportRepository.findByPersonalData_DocumentId(documentId);

        AIReport report = existingReport.orElse(new AIReport());
        report.setPersonalData(personalData);
        report.setReportData(reportData);

        AIReport saved = aiReportRepository.save(report);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<?> getReportByDocumentId(@PathVariable String documentId) {
        Optional<AIReport> reportOpt = aiReportRepository.findByPersonalData_DocumentId(documentId);
        return reportOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteReportByDocumentId(@PathVariable String documentId) {
        Optional<AIReport> reportOpt = aiReportRepository.findByPersonalData_DocumentId(documentId);
        if (reportOpt.isPresent()) {
            aiReportRepository.delete(reportOpt.get());
            return ResponseEntity.ok().body("Reporte eliminado correctamente.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping
    public ResponseEntity<List<AIReport>> getAllReports() {
        List<AIReport> reports = aiReportRepository.findAll();
        return ResponseEntity.ok(reports);
    }
}
