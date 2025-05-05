package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import proymodpredictivoia.demo.model.AuthUser;
import proymodpredictivoia.demo.repository.AuthUserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthUserController {

    @Autowired
    private AuthUserRepository authUserRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthUser user) {
        // Verifica si el usuario ya existe por su documentId
        if (authUserRepository.existsByDocumentId(user.getDocumentId())) {
            return ResponseEntity.badRequest().body("El usuario ya existe con ese documentId.");
        }

        authUserRepository.save(user);
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }
}
