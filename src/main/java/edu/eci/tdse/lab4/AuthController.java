package edu.eci.tdse.lab4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Registrar usuario con password hasheado
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Usuario ya existe");
        }

        // BCrypt hashea automáticamente con salt
        String hashed = encoder.encode(password);
        userRepository.save(new User(username, hashed));

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    // Login: verifica password contra el hash almacenado
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        boolean matches = encoder.matches(password, userOpt.get().getPasswordHash());

        if (matches) {
            return ResponseEntity.ok("Login exitoso para: " + username);
        } else {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }
    }
}