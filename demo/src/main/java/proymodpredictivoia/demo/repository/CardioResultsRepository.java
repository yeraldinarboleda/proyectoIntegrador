package proymodpredictivoia.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proymodpredictivoia.demo.model.CardioResults;

@Repository
public interface CardioResultsRepository extends JpaRepository<CardioResults, Long> {
    List<CardioResults> findByDocumentId(String documentId);
}

