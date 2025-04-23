package proymodpredictivoia.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;  // 📌 Para manejar las fechas de forma correcta
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "personal_data")
@Data
public class PersonalData {

    @Id  // 📌 Establecer el número de documento como clave primaria
    private String documentId;

    @Column(nullable = false)
    private String documentType;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    private LocalDate birthDate;  // 📌 Usar LocalDate para fechas
    
    private String gender;
    
    private String address;
    
    private String contact;
    
    private String gmail;

    // Getters y setters generados por Lombok (@Data)

    @OneToOne(mappedBy = "personalData")
    private PatientRecord patientRecord;

}
