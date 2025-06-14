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
@Table(name = "cardio_results")
@Getter
@Setter
@NoArgsConstructor
public class CardioResults {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private String documentId;
    
    @Column(name = "electrocardiogram", nullable = false)
    private String electrocardiogram;
    
    @Column(name = "echocardiogram", nullable = false)
    private String echocardiogram;

    @Column(name = "chest_pain_type", nullable = true)
    private Integer chestPainType;

    @Column(name = "resting_ecg", nullable = true)
    private Integer restingECG;

    @Column(name = "exercise_angina", nullable = true)
    private Integer exerciseAngina;

}

