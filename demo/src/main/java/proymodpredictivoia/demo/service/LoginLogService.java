package proymodpredictivoia.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proymodpredictivoia.demo.model.LoginLog;
import proymodpredictivoia.demo.repository.LoginLogRepository;

import java.time.LocalDateTime;

@Service
public class LoginLogService {

    @Autowired
    private LoginLogRepository loginLogRepository;

    public void logLoginAttempt(String documentId, boolean success, String failureReason,
                                String ipAddress, String userAgent) {
        LoginLog log = new LoginLog();
        log.setDocumentId(documentId);
        log.setLoginTime(LocalDateTime.now());
        log.setSuccess(success);
        log.setFailureReason(failureReason);

        loginLogRepository.save(log);
    }
}
