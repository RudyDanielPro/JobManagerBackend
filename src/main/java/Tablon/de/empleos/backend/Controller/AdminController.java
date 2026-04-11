package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.request.AdminUserCreateRequest;
import Tablon.de.empleos.backend.DTO.request.AdminUserUpdateRequest;
import Tablon.de.empleos.backend.Entity.*;
import Tablon.de.empleos.backend.Repository.*;
import Tablon.de.empleos.backend.Services.CandidatoService;
import Tablon.de.empleos.backend.Services.EmpresaService;
import Tablon.de.empleos.backend.Services.UserService;
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
    
    // Servicios necesarios
    private final UserService userService;
    private final CandidatoService candidatoService;
    private final EmpresaService empresaService;

    public AdminController(UserRepository userRepository,
                           EmpresaRepository empresaRepository,
                           OfertaLaboralRepository ofertaRepository,
                           PostulacionRepository postulacionRepository,
                           CandidatoRepository candidatoRepository,
                           AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder,
                           UserService userService,
                           CandidatoService candidatoService,
                           EmpresaService empresaService) {
        this.userRepository = userRepository;
        this.empresaRepository = empresaRepository;
        this.ofertaRepository = ofertaRepository;
        this.postulacionRepository = postulacionRepository;
        this.candidatoRepository = candidatoRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.candidatoService = candidatoService;
        this.empresaService = empresaService;
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

    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody AdminUserCreateRequest request) {
        // Validaciones básicas
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("La contraseña es obligatoria");
        }
        if (userService.existePorEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya está registrado");
        }
        if (userService.existePorUsuario(request.getUsuario())) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }

        // Crear el usuario base
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsuario(request.getUsuario());
        user.setPassword(request.getPassword()); // Se encriptará en el servicio
        user.setRol(request.getRol().toUpperCase());

        try {
            if ("CANDIDATO".equalsIgnoreCase(request.getRol())) {
                // Validar nombre y apellido
                if (request.getNombre() == null || request.getApellido() == null) {
                    return ResponseEntity.badRequest().body("Nombre y apellido son obligatorios para candidato");
                }
                candidatoService.registrarCandidato(user, request.getNombre(), request.getApellido(), null);
            } else if ("RECRUITER".equalsIgnoreCase(request.getRol())) {
                if (request.getNombreEmpresa() == null) {
                    return ResponseEntity.badRequest().body("Nombre de empresa es obligatorio para reclutador");
                }
                empresaService.registrarEmpresa(user, request.getNombreEmpresa(),
                        request.getDescripcion(), request.getUrl(), null);
            } else if ("ADMIN".equalsIgnoreCase(request.getRol())) {
                if (request.getNombre() == null || request.getApellido() == null) {
                    return ResponseEntity.badRequest().body("Nombre y apellido son obligatorios para administrador");
                }
                // Crear admin manualmente
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                User savedUser = userRepository.save(user);
                Admin admin = new Admin(request.getNombre(), request.getApellido());
                admin.setId(savedUser.getId());
                admin.setUsuario(savedUser);
                adminRepository.save(admin);
                savedUser.setAdmin(admin);
                userRepository.save(savedUser);
            } else {
                return ResponseEntity.badRequest().body("Rol no válido");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear usuario: " + e.getMessage());
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody AdminUserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos básicos
        if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
        if (request.getUsuario() != null) existingUser.setUsuario(request.getUsuario());
        if (request.getRol() != null) existingUser.setRol(request.getRol());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        User savedUser = userRepository.save(existingUser);
        
        // Actualizar datos específicos según rol
        if ("CANDIDATO".equalsIgnoreCase(savedUser.getRol())) {
            Candidato candidato = candidatoRepository.findById(id).orElse(new Candidato());
            if (request.getNombre() != null) candidato.setNombre(request.getNombre());
            if (request.getApellido() != null) candidato.setApellido(request.getApellido());
            candidato.setId(id);
            candidato.setUsuario(savedUser);
            candidatoRepository.save(candidato);
            savedUser.setCandidato(candidato);
        } 
        else if ("RECRUITER".equalsIgnoreCase(savedUser.getRol())) {
            Empresa empresa = empresaRepository.findById(id).orElse(new Empresa());
            if (request.getNombreEmpresa() != null) empresa.setNombreEmpresa(request.getNombreEmpresa());
            if (request.getDescripcion() != null) empresa.setDescripcion(request.getDescripcion());
            if (request.getUrl() != null) empresa.setUrl(request.getUrl());
            empresa.setId(id);
            empresa.setUsuario(savedUser);
            empresaRepository.save(empresa);
            savedUser.setEmpresa(empresa);
        }
        else if ("ADMIN".equalsIgnoreCase(savedUser.getRol())) {
            Admin admin = adminRepository.findById(id).orElse(new Admin());
            if (request.getNombre() != null) admin.setNombre(request.getNombre());
            if (request.getApellido() != null) admin.setApellido(request.getApellido());
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
    public ResponseEntity<Postulacion> cambiarEstadoPostulacion(@PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
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
        if (adminData.getNombre() != null)
            admin.setNombre(adminData.getNombre());
        if (adminData.getApellido() != null)
            admin.setApellido(adminData.getApellido());
        admin.setId(userId);
        return ResponseEntity.ok(adminRepository.save(admin));
    }
}