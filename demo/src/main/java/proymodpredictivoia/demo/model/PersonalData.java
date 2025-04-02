package proymodpredictivoia.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;  // 📌 Para manejar las fechas de forma correcta

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
}
