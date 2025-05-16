package proymodpredictivoia.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ai_reports")
@Data
public class AIReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "documentId")
    private PersonalData personalData;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reportData;  

}