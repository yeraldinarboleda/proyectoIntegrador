package proymodpredictivoia.demo.service;

import java.time.LocalDate;
import java.time.Period;

public class PersonalDataService {
    
    public int calcularEdad(LocalDate birthDate) {
    if (birthDate == null) return 0;
    return Period.between(birthDate, LocalDate.now()).getYears();
}
}
