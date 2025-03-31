package proymodpredictivoia.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "personal_data")
@Data
public class PersonalData {

    @Id  // 📌 Establecer el número de documento como clave primaria
    private String documentId;

    private String documentType;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String gender;
    private String address;
    private String contact;
    private String gmail;
}
