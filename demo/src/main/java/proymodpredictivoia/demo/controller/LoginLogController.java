package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proymodpredictivoia.demo.model.LoginLog;
import proymodpredictivoia.demo.repository.LoginLogRepository;

import java.util.List;

@RestController
@RequestMapping("/api/login-logs")
public class LoginLogController {

    @Autowired
    private LoginLogRepository loginLogRepository;

    // 🔹 Ver todos los logs
    @GetMapping
    public ResponseEntity<List<LoginLog>> getAllLogs() {
        return ResponseEntity.ok(loginLogRepository.findAll());
    }

    // 🔹 Ver logs por documento del médico
    @GetMapping("/{documentId}")
    public ResponseEntity<List<LoginLog>> getLogsByDocument(@PathVariable String documentId) {
        List<LoginLog> logs = loginLogRepository.findAll()
                .stream()
                .filter(log -> documentId.equals(log.getDocumentId()))
                .toList();

        return ResponseEntity.ok(logs);
    }

    // 🔹 Crear un log manualmente (por pruebas o herramientas externas)
    @PostMapping
    public ResponseEntity<LoginLog> createLog(@RequestBody LoginLog log) {
        LoginLog savedLog = loginLogRepository.save(log);
        return ResponseEntity.ok(savedLog);
    }
}
