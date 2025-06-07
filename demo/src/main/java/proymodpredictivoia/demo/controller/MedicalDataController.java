package proymodpredictivoia.demo.controller;

import proymodpredictivoia.demo.model.MedicalData;
import proymodpredictivoia.demo.repository.MedicalDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-data")
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde React
public class MedicalDataController {

    @Autowired
    private MedicalDataRepository medicalDataRepository;

    // ✅ CREATE
    @PostMapping
    public MedicalData saveMedicalData(@RequestBody MedicalData medicalData) {
        return medicalDataRepository.save(medicalData);
    }

    // ✅ READ - Todos
    @GetMapping
    public List<MedicalData> getAllMedicalData() {
        return medicalDataRepository.findAll();
    }

    // ✅ READ - Por ID
    @GetMapping("/{id}")
    public ResponseEntity<MedicalData> getMedicalDataById(@PathVariable Long id) {
        Optional<MedicalData> data = medicalDataRepository.findById(id);
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicalData(@PathVariable Long id, @RequestBody MedicalData updatedData) {
        Optional<MedicalData> optional = medicalDataRepository.findById(id);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        MedicalData data = optional.get();

        // Actualizar campos
        data.setWaistCircumference(updatedData.getWaistCircumference());
        data.setSystolicPressure(updatedData.getSystolicPressure());
        data.setDiastolicPressure(updatedData.getDiastolicPressure());
        data.setHeartRate(updatedData.getHeartRate());
        data.setWeight(updatedData.getWeight());
        data.setHeight(updatedData.getHeight());
        data.setBmi(updatedData.getBmi());
        data.setRestingBP(updatedData.getRestingBP());
        data.setSerumCholesterol(updatedData.getSerumCholesterol());
        data.setMaxHeartRate(updatedData.getMaxHeartRate());
        data.setFastingBloodSugar(updatedData.getFastingBloodSugar());
        data.setOldpeak(updatedData.getOldpeak());
        data.setSlope(updatedData.getSlope());
        data.setNoOfMajorVessels(updatedData.getNoOfMajorVessels());

        medicalDataRepository.save(data);
        return ResponseEntity.ok("Datos médicos actualizados correctamente.");
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicalData(@PathVariable Long id) {
        if (!medicalDataRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        medicalDataRepository.deleteById(id);
        return ResponseEntity.ok("Datos médicos eliminados correctamente.");
    }
}
