package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Para que tu React no llore por el CORS
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestPart("usuario") User usuario, 
                                       @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        try {
            User nuevoUsuario = usuarioService.registrarUsuario(usuario, archivo);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir la imagen a Cloudinary");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = usuarioService.autenticarUsuario(loginRequest.getIdentificador(), loginRequest.getPassword());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}