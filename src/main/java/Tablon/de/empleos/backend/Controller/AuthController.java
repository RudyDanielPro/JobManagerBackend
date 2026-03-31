package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Para que tu React no llore por el CORS
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping(value = "/registro", consumes = { "multipart/form-data" })
    public ResponseEntity<?> registrar(
            @RequestPart("usuario") String usuarioJson, // Recibimos como String para deserializar manualmente o dejar
                                                        // que Jackson lo haga
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User usuario = objectMapper.readValue(usuarioJson, User.class);

            User nuevoUsuario = usuarioService.registrarUsuario(usuario, archivo);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al procesar el registro: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest credentials) {
        // Usamos el método que SI verifica el password
        User user = usuarioService.autenticarUsuario(credentials.getIdentificador(), credentials.getPassword());

        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
    }
}