package proymodpredictivoia.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proymodpredictivoia.demo.model.LabResults;

@Repository
public interface LabResultsRepository extends JpaRepository<LabResults, Long> {
    List<LabResults> findByDocumentId(String documentId);
}