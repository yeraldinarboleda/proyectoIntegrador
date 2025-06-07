package proymodpredictivoia.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "personal_data")
@Data
public class PersonalData {

    @Id
    private String documentId;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate birthDate;

    // gender: "masculino" o "femenino", lo mapeas en servicio a 1 o 0 según el modelo
    private String gender;

    private String address;

    private String contact;

    private String gmail;

    @OneToOne(mappedBy = "personalData")
    private PatientRecord patientRecord;


    
}
