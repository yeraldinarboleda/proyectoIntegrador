package proymodpredictivoia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proymodpredictivoia.demo.model.LoginLog;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
