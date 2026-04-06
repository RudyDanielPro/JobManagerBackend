package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.request.PostulacionRequestDTO;
import Tablon.de.empleos.backend.DTO.response.PostulacionResponseDTO;
import Tablon.de.empleos.backend.Entity.Candidato;
import Tablon.de.empleos.backend.Entity.Postulacion;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Services.CandidatoService;
import Tablon.de.empleos.backend.Services.PostulacionService;
import Tablon.de.empleos.backend.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/postulaciones")

public class PostulacionController {

    private final PostulacionService postulacionService;
    private final CandidatoService candidatoService;
    private final UserService userService;

    public PostulacionController(PostulacionService postulacionService,
            CandidatoService candidatoService,
            UserService userService) {
        this.postulacionService = postulacionService;
        this.candidatoService = candidatoService;
        this.userService = userService;
    }

    private PostulacionResponseDTO toDTO(Postulacion postulacion) {
        return new PostulacionResponseDTO(
                postulacion.getId(),
                postulacion.getFechaPostulacion(),
                postulacion.getEstado(),
                postulacion.getCandidato().getNombre() + " " + postulacion.getCandidato().getApellido(),
                postulacion.getCandidato().getUsuario().getEmail(),
                postulacion.getOfertaLaboral().getTitulo(),
                postulacion.getOfertaLaboral().getEmpresa().getNombreEmpresa(), postulacion.getOfertaLaboral().getId());
    }

    @PostMapping(value = "/enviar", consumes = { "multipart/form-data" })
    public ResponseEntity<?> enviarPostulacion(
            @ModelAttribute PostulacionRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.buscarPorEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Candidato candidato = candidatoService.buscarPorUsuarioId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Candidato no encontrado"));

            request.setEmailCandidato(user.getEmail());
            request.setNombre(candidato.getNombre());
            request.setApellido(candidato.getApellido());

            Postulacion postulacion = postulacionService.crearPostulacion(request, candidato.getId());
            return ResponseEntity.ok(toDTO(postulacion));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la postulación: " + e.getMessage());
        }
    }

    @GetMapping("/mis-postulaciones")
    public ResponseEntity<Page<PostulacionResponseDTO>> misPostulaciones(
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Candidato candidato = candidatoService.buscarPorUsuarioId(user.getId())
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado"));

        return ResponseEntity.ok(postulacionService.buscarPorCandidato(candidato.getId(), pageable).map(this::toDTO));
    }

    @GetMapping("/oferta/{ofertaId}")
    public ResponseEntity<Page<PostulacionResponseDTO>> postulacionesPorOferta(
            @PathVariable Long ofertaId, Pageable pageable) {
        return ResponseEntity.ok(postulacionService.buscarPorOferta(ofertaId, pageable).map(this::toDTO));
    }
}