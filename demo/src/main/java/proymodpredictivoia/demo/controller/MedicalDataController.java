package proymodpredictivoia.demo.controller;

import proymodpredictivoia.demo.model.MedicalData;
import proymodpredictivoia.demo.repository.MedicalDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Medical-data")
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde React
public class MedicalDataController {

    @Autowired
    private MedicalDataRepository MedicalDataRepository;

    @PostMapping
    public MedicalData saveMedicalData(@RequestBody MedicalData MedicalData) {
        return MedicalDataRepository.save(MedicalData);
    }
}
