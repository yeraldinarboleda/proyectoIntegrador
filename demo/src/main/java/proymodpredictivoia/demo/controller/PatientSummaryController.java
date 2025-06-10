package proymodpredictivoia.demo.controller;

import proymodpredictivoia.demo.DTO.PatientSummaryDTO;
import proymodpredictivoia.demo.repository.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/summary")
public class PatientSummaryController {

    @Autowired
    private PersonalDataRepository personalDataRepo;

    @Autowired
    private MedicalDataRepository medicalDataRepo;

    @Autowired
    private LabResultsRepository labResultsRepo;

    @Autowired
    private CardioResultsRepository cardioResultsRepo;

    @Autowired
    private RiskFactorsRepository riskFactorsRepo;

    @GetMapping("/{documentId}")
    public PatientSummaryDTO getPatientSummary(@PathVariable String documentId) {
        PatientSummaryDTO summary = new PatientSummaryDTO();

        summary.setPersonalData(personalDataRepo.findByDocumentId(documentId).orElse(null));
        summary.setMedicalData(medicalDataRepo.findByDocumentId(documentId));
        summary.setLabResults(labResultsRepo.findByDocumentId(documentId));
        summary.setCardioResults(cardioResultsRepo.findByDocumentId(documentId));
        summary.setRiskFactors(riskFactorsRepo.findByDocumentId(documentId));

        return summary;
    }
}
