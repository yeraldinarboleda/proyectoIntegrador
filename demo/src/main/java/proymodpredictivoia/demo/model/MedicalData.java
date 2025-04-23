package proymodpredictivoia.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medical_data")
@Getter
@Setter
@NoArgsConstructor
public class MedicalData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "waist_circumference", nullable = false)
    private Double waistCircumference;
    
    @Column(name = "systolic_pressure", nullable = false)
    private Integer systolicPressure;
    
    @Column(name = "diastolic_pressure", nullable = false)
    private Integer diastolicPressure;
    
    @Column(name = "heart_rate", nullable = false)
    private Integer heartRate;
    
    @Column(name = "weight", nullable = false)
    private Double weight;
    
    @Column(name = "height", nullable = false)
    private Double height;
    
    @Column(name = "bmi", nullable = false)
    private Double bmi;

    @OneToOne(mappedBy = "medicalData")
    private PatientRecord patientRecord;

}
