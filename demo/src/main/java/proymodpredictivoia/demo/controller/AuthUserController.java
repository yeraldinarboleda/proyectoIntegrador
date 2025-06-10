package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import proymodpredictivoia.demo.model.AuthUser;
import proymodpredictivoia.demo.repository.AuthUserRepository;
import proymodpredictivoia.demo.service.LoginLogService;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")

public class AuthUserController {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginLogService loginLogService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthUser user) {
        // Verifica si el usuario ya existe por su documentId
        if (authUserRepository.existsByDocumentId(user.getDocumentId())) {
            return ResponseEntity.badRequest().body("El usuario ya existe con ese documentId.");
        }

        // Hashear la contraseña antes de guardarla
        String hashedPassword = passwordEncoder.encode(user.getHashedPassword());
        user.setHashedPassword(hashedPassword);

        authUserRepository.save(user);
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> request,
                               @RequestHeader(value = "User-Agent", defaultValue = "unknown") String userAgent,
                               @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                               @RequestHeader(value = "Remote_Addr", required = false) String remoteAddr) {

    String documentId = request.get("documentId");
    String password = request.get("password");

    String ipAddress = forwardedFor != null ? forwardedFor : (remoteAddr != null ? remoteAddr : "unknown");

    Optional<AuthUser> optionalUser = authUserRepository.findByDocumentId(documentId);

    if (optionalUser.isEmpty()) {
        loginLogService.logLoginAttempt(documentId, false, "Usuario no encontrado", ipAddress, userAgent);
        return ResponseEntity.status(401).body("Usuario no encontrado.");
    }

    AuthUser user = optionalUser.get();

    if (!passwordEncoder.matches(password, user.getHashedPassword())) {
        loginLogService.logLoginAttempt(documentId, false, "Contraseña incorrecta", ipAddress, userAgent);
        return ResponseEntity.status(401).body("Contraseña incorrecta.");
    }

    loginLogService.logLoginAttempt(documentId, true, null, ipAddress, userAgent);
    return ResponseEntity.ok("Login exitoso");
}


    // ✅ Obtener todos los usuarios
    @GetMapping("/all")
    public List<AuthUser> getAllUsers() {
        return authUserRepository.findAll();
    }

    // ✅ Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<AuthUser> user = authUserRepository.findById(id);
        return user.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Actualizar usuario por ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AuthUser updatedUser) {
        Optional<AuthUser> optionalUser = authUserRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AuthUser user = optionalUser.get();
        user.setDocumentId(updatedUser.getDocumentId());
        user.setFullName(updatedUser.getFullName());
        user.setRole(updatedUser.getRole());
        user.setHashedPassword(updatedUser.getHashedPassword());

        authUserRepository.save(user);
        return ResponseEntity.ok("Usuario actualizado exitosamente.");
    }

    // ✅ Eliminar usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!authUserRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        authUserRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado exitosamente.");
    }

}

