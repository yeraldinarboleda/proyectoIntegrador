package proymodpredictivoia.demo.model;

import jakarta.persistence.*;
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

    // Campos agregados para modelo predictivo
    @Column(name = "resting_bp")
    private Integer restingBP;

    @Column(name = "serum_cholesterol")
    private Integer serumCholesterol;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "fasting_blood_sugar")
    private Integer fastingBloodSugar;

    @Column(name = "oldpeak")
    private Double oldpeak;

    @Column(name = "slope")
    private Integer slope;

    @Column(name = "no_of_major_vessels")
    private Integer noOfMajorVessels;

    @OneToOne(mappedBy = "medicalData")
    private PatientRecord patientRecord;
}
