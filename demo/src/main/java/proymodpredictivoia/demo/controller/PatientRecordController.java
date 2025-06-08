package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import proymodpredictivoia.demo.model.CardioResults;
import proymodpredictivoia.demo.model.LabResults;
import proymodpredictivoia.demo.model.MedicalData;
import proymodpredictivoia.demo.model.PatientRecord;
import proymodpredictivoia.demo.model.RiskFactors;
import proymodpredictivoia.demo.service.PatientRecordService;

@RestController
@RequestMapping("/patient-record")
public class PatientRecordController {

    @Autowired private PatientRecordService recordService;

    @PostMapping("/link-lab")
    public ResponseEntity<?> linkLab(@RequestParam String documentId, @RequestBody LabResults labResults) {
        PatientRecord record = recordService.linkLabResults(documentId, labResults);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/link-cardio")
    public ResponseEntity<?> linkCardio(@RequestParam String documentId, @RequestBody CardioResults cardioResults) {
        PatientRecord record = recordService.linkCardioResults(documentId, cardioResults);
        return ResponseEntity.ok(record);
    
    }

    @PostMapping("/link-risk")
    public ResponseEntity<?> linkRisk(@RequestParam String documentId, @RequestBody RiskFactors riskFactors) {
        PatientRecord record = recordService.linkRiskFactors(documentId, riskFactors);
        return ResponseEntity.ok(record);

    }


    @PostMapping("/link-medical")
    public ResponseEntity<?> linkMedical(@RequestParam String documentId, @RequestBody MedicalData medicalData) {
        PatientRecord record = recordService.linkMedicalData(documentId, medicalData);
        return ResponseEntity.ok(record);

    }
}
