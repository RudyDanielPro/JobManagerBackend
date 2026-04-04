package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.request.CandidatoUpdateRequestDTO;
import Tablon.de.empleos.backend.DTO.response.CandidatoResponseDTO;
import Tablon.de.empleos.backend.Entity.Candidato;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Services.CandidatoService;
import Tablon.de.empleos.backend.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidatos")
@CrossOrigin(origins = "*")
public class CandidatoController {

    private final CandidatoService candidatoService;
    private final UserService userService;

    public CandidatoController(CandidatoService candidatoService, UserService userService) {
        this.candidatoService = candidatoService;
        this.userService = userService;
    }

    private CandidatoResponseDTO toDTO(Candidato candidato) {
        String fotoUrl = candidato.getUsuario().getFoto() != null ? candidato.getUsuario().getFoto().getRuta() : null;
        return new CandidatoResponseDTO(
                candidato.getId(),
                candidato.getNombre(),
                candidato.getApellido(),
                candidato.getUsuario().getEmail(),
                candidato.getUsuario().getUsuario(),
                fotoUrl);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidatoResponseDTO> obtenerCandidato(@PathVariable Long id) {
        return candidatoService.buscarPorId(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mi-perfil")
    public ResponseEntity<CandidatoResponseDTO> miPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return candidatoService.buscarPorUsuarioId(user.getId())
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<CandidatoResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(candidatoService.buscarTodosPaginado(pageable).map(this::toDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidatoResponseDTO> actualizarPerfil(
            @PathVariable Long id,
            @RequestBody CandidatoUpdateRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User usuarioAutenticado = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Candidato candidato = candidatoService.actualizarPerfil(
                id, request.getNombre(), request.getApellido(), usuarioAutenticado);

        return ResponseEntity.ok(toDTO(candidato));
    }

    @PutMapping("/{id}/foto")
    public ResponseEntity<CandidatoResponseDTO> actualizarFoto(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        User usuarioAutenticado = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Candidato candidato = candidatoService.actualizarFotoPerfil(id, foto, usuarioAutenticado);
        return ResponseEntity.ok(toDTO(candidato));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCandidato(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User usuarioAutenticado = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        candidatoService.eliminarCandidato(id, usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }
}