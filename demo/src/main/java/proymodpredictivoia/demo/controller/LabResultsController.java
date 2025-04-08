package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proymodpredictivoia.demo.model.LabResults;
import proymodpredictivoia.demo.repository.LabResultsRepository;

@RestController
@RequestMapping("/api/personal-data")
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde React
public class LabResultsController {

    @Autowired
    private LabResultsRepository LabResultsRepository;

    @PostMapping
    public LabResults saveLabResults(@RequestBody LabResults LabResults) {
        return LabResultsRepository.save(LabResults);
    }
}
