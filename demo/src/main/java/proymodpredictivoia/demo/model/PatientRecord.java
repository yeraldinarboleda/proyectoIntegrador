package proymodpredictivoia.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patient_record")
@Getter
@Setter
@NoArgsConstructor
public class PatientRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con datos personales
    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "documentId")
    private PersonalData personalData;

    // Relación con resultados de laboratorio
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lab_results_id", referencedColumnName = "id")
    private LabResults labResults;

    // Relación con resultados cardíacos
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cardio_results_id", referencedColumnName = "id")
    private CardioResults cardioResults;

    // Relación con factores de riesgo
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "risk_factors_id", referencedColumnName = "id")
    private RiskFactors riskFactors;

    // Relación con datos médicos generales
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medical_data_id", referencedColumnName = "id")
    private MedicalData medicalData;
}

