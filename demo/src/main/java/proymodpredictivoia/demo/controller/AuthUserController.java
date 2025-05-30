package proymodpredictivoia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import proymodpredictivoia.demo.model.AuthUser;
import proymodpredictivoia.demo.repository.AuthUserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String documentId = request.get("documentId");
        String password = request.get("password");

        // Buscar usuario por documentId
        Optional<AuthUser> optionalUser = authUserRepository.findByDocumentId(documentId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no encontrado.");
        }

        AuthUser user = optionalUser.get();

        // Verificar contraseña (de forma simple, sin hashing por ahora)
        if (!user.getHashedPassword().equals(password)) {
            return ResponseEntity.status(401).body("Contraseña incorrecta.");
        }

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

