package proymodpredictivoia.demo.repository;

import proymodpredictivoia.demo.model.PersonalData;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDataRepository extends JpaRepository<PersonalData, String> {
    Optional<PersonalData> findByDocumentId(String documentId);
}
