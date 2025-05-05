package proymodpredictivoia.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "login_logs")
@Data
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String documentId;

    @Column(nullable = false)
    private LocalDateTime loginTime;

    @Column(nullable = false)
    private Boolean success;

    private String failureReason;

    @ManyToOne
    @JoinColumn(name = "documentId", referencedColumnName = "documentId", insertable = false, updatable = false)
    private AuthUser authUser;

}
