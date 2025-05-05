package proymodpredictivoia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proymodpredictivoia.demo.model.AuthUser;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByDocumentId(String documentId);
    boolean existsByDocumentId(String documentId);
}
