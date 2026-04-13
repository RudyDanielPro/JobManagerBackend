package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.request.OfertaRequestDTO;
import Tablon.de.empleos.backend.DTO.response.OfertaResponseDTO;
import Tablon.de.empleos.backend.Entity.Candidato;
import Tablon.de.empleos.backend.Entity.OfertaLaboral;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Services.CandidatoService;
import Tablon.de.empleos.backend.Services.EmpresaService;
import Tablon.de.empleos.backend.Services.OfertaLaboralService;
import Tablon.de.empleos.backend.Services.PostulacionService;
import Tablon.de.empleos.backend.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaLaboralController {

    private final OfertaLaboralService ofertaService;
    private final EmpresaService empresaService;
    private final UserService userService;
    private final CandidatoService candidatoService;
    private final PostulacionService postulacionService;

    public OfertaLaboralController(OfertaLaboralService ofertaService,
            EmpresaService empresaService,
            UserService userService,
            CandidatoService candidatoService,
            PostulacionService postulacionService) {
        this.ofertaService = ofertaService;
        this.empresaService = empresaService;
        this.userService = userService;
        this.candidatoService = candidatoService;
        this.postulacionService = postulacionService;
    }

    private OfertaResponseDTO toDTO(OfertaLaboral oferta) {
        return new OfertaResponseDTO(
                oferta.getId(),
                oferta.getTitulo(),
                oferta.getDescripcion(),
                oferta.getUbicacion(),
                oferta.getRangoSalarial(),
                oferta.getFechaCreacion(),
                oferta.isEstado(),
                oferta.getEmpresa().getNombreEmpresa(),
                oferta.getEmpresa().getId());
    }

    // ============ ENDPOINTS PÚBLICOS ============

    @GetMapping("/public/activas")
    public ResponseEntity<Page<OfertaResponseDTO>> listarOfertasActivas(Pageable pageable) {
        return ResponseEntity.ok(ofertaService.obtenerOfertasActivas(pageable).map(this::toDTO));
    }

    @GetMapping("/public/buscar")
    public ResponseEntity<Page<OfertaResponseDTO>> buscarOfertas(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String ubicacion,
            Pageable pageable) {

        Page<OfertaLaboral> ofertas;
        if (titulo != null && ubicacion != null) {
            ofertas = ofertaService.buscarPorTituloYUbicacion(titulo, ubicacion, pageable);
        } else if (titulo != null) {
            ofertas = ofertaService.buscarPorTitulo(titulo, pageable);
        } else if (ubicacion != null) {
            ofertas = ofertaService.buscarPorUbicacion(ubicacion, pageable);
        } else {
            ofertas = ofertaService.obtenerOfertasActivas(pageable);
        }
        return ResponseEntity.ok(ofertas.map(this::toDTO));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<OfertaResponseDTO> obtenerOfertaPublica(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {  // ✅ Añadir usuario autenticado (opcional)

        OfertaLaboral oferta = ofertaService.obtenerPorId(id)
                .orElse(null);

        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }

        // ✅ Si el usuario está autenticado y es CANDIDATO, verificar si aplicó a esta oferta
        if (userDetails != null) {
            try {
                User user = userService.buscarPorEmail(userDetails.getUsername())
                        .orElse(null);

                if (user != null && "CANDIDATO".equalsIgnoreCase(user.getRol())) {
                    Candidato candidato = candidatoService.buscarPorUsuarioId(user.getId())
                            .orElse(null);

                    if (candidato != null) {
                        // Verificar si el candidato aplicó a esta oferta
                        boolean yaAplico = postulacionService.yaSePostulo(candidato.getId(), id);

                        // ✅ Si aplicó, permitir ver la oferta aunque esté inactiva
                        if (yaAplico) {
                            return ResponseEntity.ok(toDTO(oferta));
                        }
                    }
                }
            } catch (Exception e) {
                // Si hay algún error, continuar con la lógica normal
                System.err.println("Error verificando postulación: " + e.getMessage());
            }
        }

        // ✅ Para usuarios no autenticados o que no aplicaron, solo mostrar ofertas activas
        if (!oferta.isEstado()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDTO(oferta));
    }

    // ============ ENDPOINTS PROTEGIDOS ============

    @GetMapping("/empresa/mis-ofertas")
    public ResponseEntity<Page<OfertaResponseDTO>> misOfertas(
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long empresaId = empresaService.buscarPorUsuarioId(user.getId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"))
                .getId();

        return ResponseEntity.ok(ofertaService.obtenerPorEmpresa(empresaId, pageable).map(this::toDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfertaResponseDTO> obtenerOferta(@PathVariable Long id) {
        return ofertaService.obtenerPorId(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OfertaResponseDTO> crearOferta(
            @RequestBody OfertaRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long empresaId = empresaService.buscarPorUsuarioId(user.getId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"))
                .getId();

        OfertaLaboral oferta = new OfertaLaboral();
        oferta.setTitulo(request.getTitulo());
        oferta.setDescripcion(request.getDescripcion());
        oferta.setUbicacion(request.getUbicacion());
        oferta.setRangoSalarial(request.getRangoSalarial());

        OfertaLaboral nuevaOferta = ofertaService.publicarOferta(oferta, empresaId);
        return ResponseEntity.ok(toDTO(nuevaOferta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfertaResponseDTO> actualizarOferta(
            @PathVariable Long id,
            @RequestBody OfertaRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long empresaId = empresaService.buscarPorUsuarioId(user.getId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"))
                .getId();

        OfertaLaboral oferta = new OfertaLaboral();
        oferta.setTitulo(request.getTitulo());
        oferta.setDescripcion(request.getDescripcion());
        oferta.setUbicacion(request.getUbicacion());
        oferta.setRangoSalarial(request.getRangoSalarial());

        OfertaLaboral ofertaActualizada = ofertaService.actualizarOferta(id, oferta, empresaId);
        return ResponseEntity.ok(toDTO(ofertaActualizada));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<OfertaResponseDTO> activarOferta(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long empresaId = empresaService.buscarPorUsuarioId(user.getId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"))
                .getId();

        OfertaLaboral oferta = ofertaService.activarOferta(id, empresaId);
        return ResponseEntity.ok(toDTO(oferta));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<OfertaResponseDTO> desactivarOferta(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long empresaId = empresaService.buscarPorUsuarioId(user.getId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"))
                .getId();

        OfertaLaboral oferta = ofertaService.desactivarOferta(id, empresaId);
        return ResponseEntity.ok(toDTO(oferta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOferta(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long empresaId = empresaService.buscarPorUsuarioId(user.getId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"))
                .getId();

        ofertaService.eliminarOferta(id, empresaId);
        return ResponseEntity.noContent().build();
    }
}