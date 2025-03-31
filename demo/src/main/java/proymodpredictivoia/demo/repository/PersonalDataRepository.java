package proymodpredictivoia.demo.repository;

import proymodpredictivoia.demo.model.PersonalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDataRepository extends JpaRepository<PersonalData, String> {
}
