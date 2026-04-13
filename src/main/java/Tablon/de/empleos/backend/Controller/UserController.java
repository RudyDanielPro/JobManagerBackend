package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.response.UserResponseDTO;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private UserResponseDTO toDTO(User user) {
        String fotoUrl = user.getFoto() != null ? user.getFoto().getRuta() : null;
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsuario(),
                user.getRol(),
                fotoUrl
        );
    }

    /**
     * Actualizar foto de perfil de un usuario
     * Endpoint: PUT /api/usuarios/{id}/foto
     */
    @PutMapping("/{id}/foto")
    public ResponseEntity<?> actualizarFoto(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Obtener el usuario autenticado
            User usuarioAutenticado = userService.buscarPorEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar permisos: solo el propio usuario o ADMIN pueden cambiar la foto
            boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());
            boolean esMismoUsuario = usuarioAutenticado.getId().equals(id);

            if (!esAdmin && !esMismoUsuario) {
                return ResponseEntity.status(403).body("No tienes permisos para cambiar esta foto");
            }

            // Validar que el archivo no esté vacío
            if (foto == null || foto.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo de foto es obligatorio");
            }

            // Actualizar la foto
            User usuarioActualizado = userService.actualizarFotoPerfil(id, foto, usuarioAutenticado);
            
            return ResponseEntity.ok(toDTO(usuarioActualizado));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al subir la foto: " + e.getMessage());
        }
    }

    /**
     * Obtener información de un usuario por ID (solo ADMIN o el propio usuario)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User usuarioAutenticado = userService.buscarPorEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());
            boolean esMismoUsuario = usuarioAutenticado.getId().equals(id);

            if (!esAdmin && !esMismoUsuario) {
                return ResponseEntity.status(403).body("No tienes permisos para ver este usuario");
            }

            User usuario = userService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            return ResponseEntity.ok(toDTO(usuario));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}