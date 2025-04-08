package proymodpredictivoia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proymodpredictivoia.demo.model.RiskFactors;

@Repository
public interface RiskFactorsRepository extends JpaRepository<RiskFactors, String> {
}
