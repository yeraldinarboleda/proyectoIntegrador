package proymodpredictivoia.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
@NoArgsConstructor
public class LabResults {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "hemoglobin_a1c", nullable = false)
    private Double hemoglobinA1c;
    
    @Column(name = "fasting_glucose", nullable = false)
    private Double fastingGlucose;
    
    @Column(name = "total_cholesterol", nullable = false)
    private Double totalCholesterol;
    
    @Column(name = "hdl", nullable = false)
    private Double hdl;
    
    @Column(name = "ldl", nullable = false)
    private Double ldl;
    
    @Column(name = "triglycerides", nullable = false)
    private Double triglycerides;
    
    @Column(name = "creatinine", nullable = false)
    private Double creatinine;
}
