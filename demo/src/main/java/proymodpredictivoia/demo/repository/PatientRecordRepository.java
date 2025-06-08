package proymodpredictivoia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import proymodpredictivoia.demo.model.PatientRecord;
import proymodpredictivoia.demo.model.PersonalData;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {
    Optional<PatientRecord> findByPersonalData(PersonalData personalData);
    Optional<PatientRecord> findByPersonalData_DocumentId(String documentId);
}
