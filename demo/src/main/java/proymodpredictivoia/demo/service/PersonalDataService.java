package proymodpredictivoia.demo.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class PersonalDataService {
    
    public int calcularEdad(LocalDate birthDate) {
    if (birthDate == null) return 0;
    return Period.between(birthDate, LocalDate.now()).getYears();
}
}
