package proymodpredictivoia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proymodpredictivoia.demo.model.MedicalData;

@Repository
public interface MedicalDataRepository extends JpaRepository<MedicalData, Long> {
}
