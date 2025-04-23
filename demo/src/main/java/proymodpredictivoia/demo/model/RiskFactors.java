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
@Table(name = "risk_factors")
@Getter
@Setter
@NoArgsConstructor
public class RiskFactors {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "smoking", nullable = false)
    private Boolean smoking;
    
    @Column(name = "drug_use", nullable = false)
    private Boolean drugUse;
    
    @Column(name = "alcohol_consumption", nullable = false)
    private Boolean alcoholConsumption;
    
    @Column(name = "physical_activity", nullable = false)
    private String physicalActivity;
    
    @Column(name = "diet", nullable = false)
    private String diet;
    
    @Column(name = "diabetes", nullable = false)
    private Boolean diabetes;
    
    @Column(name = "diabetes_type")
    private String diabetesType;
    
    @Column(name = "high_cholesterol", nullable = false)
    private Boolean highCholesterol;
    
    @Column(name = "hypertension", nullable = false)
    private Boolean hypertension;
    
    @Column(name = "medication_use",nullable = false)
    private Boolean medicationUse;
    
    @Column(name = "cardiovascular_diseases",nullable = false)
    private Boolean cardiovascularDiseases;
    
    @Column(name = "cardiovascular_disease_type")
    private String cardiovascularDiseaseType;
    
    @Column(name = "other_cardiovascular_diseases")
    private String otherCardiovascularDiseases;

    @OneToOne(mappedBy = "riskFactors")
    private PatientRecord patientRecord;

}