package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proymodpredictivoia.demo.model.CardioResults;
import proymodpredictivoia.demo.repository.CardioResultsRepository;

@RestController
@RequestMapping("/api/personal-data")
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde React
public class CardioResultsController {

    @Autowired
    private CardioResultsRepository CardioResultsRepository;

    @PostMapping
    public CardioResults saveCardioResults(@RequestBody CardioResults CardioResults) {
        return CardioResultsRepository.save(CardioResults);
    }
}

