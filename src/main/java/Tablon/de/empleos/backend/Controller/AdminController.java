package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.response.*;
import Tablon.de.empleos.backend.Entity.*;
import Tablon.de.empleos.backend.Repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final EmpresaRepository empresaRepository;
    private final OfertaLaboralRepository ofertaRepository;
    private final PostulacionRepository postulacionRepository;
    private final CandidatoRepository candidatoRepository;

    public AdminController(UserRepository userRepository,
                           EmpresaRepository empresaRepository,
                           OfertaLaboralRepository ofertaRepository,
                           PostulacionRepository postulacionRepository,
                           CandidatoRepository candidatoRepository) {
        this.userRepository = userRepository;
        this.empresaRepository = empresaRepository;
        this.ofertaRepository = ofertaRepository;
        this.postulacionRepository = postulacionRepository;
        this.candidatoRepository = candidatoRepository;
    }

    // ==================== ESTADÍSTICAS ====================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long totalCandidates = candidatoRepository.count();
        long totalCompanies = empresaRepository.count();
        long totalOffers = ofertaRepository.count();
        long activeOffers = ofertaRepository.countByEstadoTrue();
        long totalPostulations = postulacionRepository.count();
        long pendingPostulations = postulacionRepository.countByEstadoFalse();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalCandidates", totalCandidates);
        stats.put("totalCompanies", totalCompanies);
        stats.put("totalOffers", totalOffers);
        stats.put("activeOffers", activeOffers);
        stats.put("totalPostulations", totalPostulations);
        stats.put("pendingPostulations", pendingPostulations);
        
        return ResponseEntity.ok(stats);
    }

    // ==================== USUARIOS ====================

    @GetMapping("/usuarios")
    public ResponseEntity<Page<User>> getUsuarios(Pageable pageable) {
        return ResponseEntity.ok(userRepository.findAll(pageable));
    }

    @PatchMapping("/usuarios/{id}/rol")
    public ResponseEntity<User> cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String nuevoRol = body.get("rol");
        user.setRol(nuevoRol);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== EMPRESAS ====================

    @GetMapping("/empresas")
    public ResponseEntity<Page<Empresa>> getEmpresas(Pageable pageable) {
        return ResponseEntity.ok(empresaRepository.findAll(pageable));
    }

    @DeleteMapping("/empresas/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) {
        empresaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== OFERTAS ====================

    @GetMapping("/ofertas")
    public ResponseEntity<Page<OfertaLaboral>> getOfertas(Pageable pageable) {
        return ResponseEntity.ok(ofertaRepository.findAll(pageable));
    }

    @PatchMapping("/ofertas/{id}/toggle")
    public ResponseEntity<OfertaLaboral> toggleOferta(@PathVariable Long id) {
        OfertaLaboral oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));
        oferta.setEstado(!oferta.isEstado());
        return ResponseEntity.ok(ofertaRepository.save(oferta));
    }

    @DeleteMapping("/ofertas/{id}")
    public ResponseEntity<Void> eliminarOferta(@PathVariable Long id) {
        ofertaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== POSTULACIONES ====================

    @GetMapping("/postulaciones")
    public ResponseEntity<Page<Postulacion>> getPostulaciones(Pageable pageable) {
        return ResponseEntity.ok(postulacionRepository.findAll(pageable));
    }

    @PatchMapping("/postulaciones/{id}/estado")
    public ResponseEntity<Postulacion> cambiarEstadoPostulacion(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Postulacion postulacion = postulacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));
        postulacion.setEstado(body.get("estado"));
        return ResponseEntity.ok(postulacionRepository.save(postulacion));
    }
}