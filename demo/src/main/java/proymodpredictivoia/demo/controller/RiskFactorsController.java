package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proymodpredictivoia.demo.model.RiskFactors;
import proymodpredictivoia.demo.repository.RiskFactorsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/risk-factors")
@CrossOrigin(origins = "http://localhost:3000")
public class RiskFactorsController {

    @Autowired
    private RiskFactorsRepository riskFactorsRepository;

    // ✅ CREATE
    @PostMapping
    public RiskFactors saveRiskFactors(@RequestBody RiskFactors riskFactors) {
        return riskFactorsRepository.save(riskFactors);
    }

    // ✅ READ - Todos
    @GetMapping
    public List<RiskFactors> getAllRiskFactors() {
        return riskFactorsRepository.findAll();
    }

    // ✅ READ - Por ID
    @GetMapping("/{id}")
    public ResponseEntity<RiskFactors> getRiskFactorsById(@PathVariable Long id) {
        return riskFactorsRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ UPDATE - Por ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRiskFactors(@PathVariable Long id, @RequestBody RiskFactors updated) {
        Optional<RiskFactors> optional = riskFactorsRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RiskFactors existing = optional.get();
        existing.setSmoking(updated.getSmoking());
        existing.setDrugUse(updated.getDrugUse());
        existing.setAlcoholConsumption(updated.getAlcoholConsumption());
        existing.setPhysicalActivity(updated.getPhysicalActivity());
        existing.setDiet(updated.getDiet());
        existing.setDiabetes(updated.getDiabetes());
        existing.setDiabetesType(updated.getDiabetesType());
        existing.setHighCholesterol(updated.getHighCholesterol());
        existing.setHypertension(updated.getHypertension());
        existing.setMedicationUse(updated.getMedicationUse());
        existing.setCardiovascularDiseases(updated.getCardiovascularDiseases());
        existing.setCardiovascularDiseaseType(updated.getCardiovascularDiseaseType());
        existing.setOtherCardiovascularDiseases(updated.getOtherCardiovascularDiseases());

        riskFactorsRepository.save(existing);
        return ResponseEntity.ok("Factores de riesgo actualizados correctamente.");
    }

    // ✅ DELETE - Por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRiskFactors(@PathVariable Long id) {
        if (!riskFactorsRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        riskFactorsRepository.deleteById(id);
        return ResponseEntity.ok("Factores de riesgo eliminados correctamente.");
    }
}
