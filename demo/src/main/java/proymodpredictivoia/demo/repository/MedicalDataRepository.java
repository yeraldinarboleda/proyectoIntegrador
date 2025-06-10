package proymodpredictivoia.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proymodpredictivoia.demo.model.MedicalData;



@Repository
public interface MedicalDataRepository extends JpaRepository<MedicalData, Long> {
    List<MedicalData> findByDocumentId(String documentId);
    
}
