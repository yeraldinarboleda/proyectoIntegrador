package proymodpredictivoia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proymodpredictivoia.demo.model.AIReport;

import java.util.Optional;

public interface AIReportRepository extends JpaRepository<AIReport, Long> {
    Optional<AIReport> findByPersonalData_DocumentId(String documentId);
}
