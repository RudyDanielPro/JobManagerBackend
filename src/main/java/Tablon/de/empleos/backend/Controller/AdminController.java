package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.response.*;
import Tablon.de.empleos.backend.Entity.*;
import Tablon.de.empleos.backend.Repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final EmpresaRepository empresaRepository;
    private final OfertaLaboralRepository ofertaRepository;
    private final PostulacionRepository postulacionRepository;
    private final CandidatoRepository candidatoRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository,
                           EmpresaRepository empresaRepository,
                           OfertaLaboralRepository ofertaRepository,
                           PostulacionRepository postulacionRepository,
                           CandidatoRepository candidatoRepository,
                           AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.empresaRepository = empresaRepository;
        this.ofertaRepository = ofertaRepository;
        this.postulacionRepository = postulacionRepository;
        this.candidatoRepository = candidatoRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== ESTADÍSTICAS ====================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long totalCandidates = candidatoRepository.count();
        long totalCompanies = empresaRepository.count();
        long totalAdmins = adminRepository.count();
        long totalOffers = ofertaRepository.count();
        long activeOffers = ofertaRepository.countByEstadoTrue();
        long totalPostulations = postulacionRepository.count();
        long pendingPostulations = postulacionRepository.countByEstadoFalse();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalCandidates", totalCandidates);
        stats.put("totalCompanies", totalCompanies);
        stats.put("totalAdmins", totalAdmins);
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

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<User> getUsuarioById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ NUEVO: Crear usuario (con soporte para candidato, reclutador, admin)
    @PostMapping("/usuarios")
    public ResponseEntity<User> crearUsuario(@RequestBody User user) {
        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        // Si es candidato, crear Candidato asociado
        if ("CANDIDATO".equalsIgnoreCase(user.getRol()) && user.getCandidato() != null) {
            Candidato candidato = user.getCandidato();
            candidato.setId(savedUser.getId());
            candidato.setUsuario(savedUser);
            candidatoRepository.save(candidato);
            savedUser.setCandidato(candidato);
        }
        
        // Si es reclutador, crear Empresa asociada
        if ("RECRUITER".equalsIgnoreCase(user.getRol()) && user.getEmpresa() != null) {
            Empresa empresa = user.getEmpresa();
            empresa.setId(savedUser.getId());
            empresa.setUsuario(savedUser);
            empresaRepository.save(empresa);
            savedUser.setEmpresa(empresa);
        }
        
        // Si es admin, crear Admin asociado
        if ("ADMIN".equalsIgnoreCase(user.getRol()) && user.getAdmin() != null) {
            Admin admin = user.getAdmin();
            admin.setId(savedUser.getId());
            admin.setUsuario(savedUser);
            adminRepository.save(admin);
            savedUser.setAdmin(admin);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // ✅ NUEVO: Actualizar usuario
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<User> actualizarUsuario(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
        if (user.getUsuario() != null) existingUser.setUsuario(user.getUsuario());
        if (user.getRol() != null) existingUser.setRol(user.getRol());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        User savedUser = userRepository.save(existingUser);
        
        // Actualizar Candidato si existe
        if ("CANDIDATO".equalsIgnoreCase(savedUser.getRol()) && user.getCandidato() != null) {
            Candidato candidato = candidatoRepository.findById(id)
                    .orElse(new Candidato());
            if (user.getCandidato().getNombre() != null) candidato.setNombre(user.getCandidato().getNombre());
            if (user.getCandidato().getApellido() != null) candidato.setApellido(user.getCandidato().getApellido());
            candidato.setId(id);
            candidato.setUsuario(savedUser);
            candidatoRepository.save(candidato);
            savedUser.setCandidato(candidato);
        }
        
        // Actualizar Empresa si existe
        if ("RECRUITER".equalsIgnoreCase(savedUser.getRol()) && user.getEmpresa() != null) {
            Empresa empresa = empresaRepository.findById(id)
                    .orElse(new Empresa());
            if (user.getEmpresa().getNombreEmpresa() != null) empresa.setNombreEmpresa(user.getEmpresa().getNombreEmpresa());
            if (user.getEmpresa().getDescripcion() != null) empresa.setDescripcion(user.getEmpresa().getDescripcion());
            if (user.getEmpresa().getUrl() != null) empresa.setUrl(user.getEmpresa().getUrl());
            empresa.setId(id);
            empresa.setUsuario(savedUser);
            empresaRepository.save(empresa);
            savedUser.setEmpresa(empresa);
        }
        
        // Actualizar Admin si existe
        if ("ADMIN".equalsIgnoreCase(savedUser.getRol()) && user.getAdmin() != null) {
            Admin admin = adminRepository.findById(id)
                    .orElse(new Admin());
            if (user.getAdmin().getNombre() != null) admin.setNombre(user.getAdmin().getNombre());
            if (user.getAdmin().getApellido() != null) admin.setApellido(user.getAdmin().getApellido());
            admin.setId(id);
            admin.setUsuario(savedUser);
            adminRepository.save(admin);
            savedUser.setAdmin(admin);
        }
        
        return ResponseEntity.ok(savedUser);
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

    // ==================== ADMIN ====================

    @GetMapping("/usuarios/{userId}/admin")
    public ResponseEntity<Admin> getAdminByUserId(@PathVariable Long userId) {
        return adminRepository.findByUsuarioId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuarios/{userId}/admin")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long userId, @RequestBody Admin adminData) {
        Admin admin = adminRepository.findByUsuarioId(userId)
                .orElse(new Admin());
        if (adminData.getNombre() != null) admin.setNombre(adminData.getNombre());
        if (adminData.getApellido() != null) admin.setApellido(adminData.getApellido());
        admin.setId(userId);
        return ResponseEntity.ok(adminRepository.save(admin));
    }
}