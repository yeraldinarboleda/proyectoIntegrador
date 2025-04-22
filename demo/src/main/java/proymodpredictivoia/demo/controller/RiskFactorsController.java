package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proymodpredictivoia.demo.model.RiskFactors;
import proymodpredictivoia.demo.repository.RiskFactorsRepository;

@RestController
@RequestMapping("/api/risk-factors")
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde React
public class RiskFactorsController {

    @Autowired
    private RiskFactorsRepository RiskFactorsRepository;

    @PostMapping
    public RiskFactors saveRiskFactors(@RequestBody RiskFactors RiskFactors) {
        return RiskFactorsRepository.save(RiskFactors);
    }
}
