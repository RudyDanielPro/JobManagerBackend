package Tablon.de.empleos.backend.Controller;

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
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> request) {
        System.out.println("=== DEBUG BACKEND ===");
        System.out.println("Request: " + request);

        // Extraer datos básicos
        String email = (String) request.get("email");
        String usuario = (String) request.get("usuario");
        String password = (String) request.get("password");
        String rol = (String) request.get("rol");

        // Validaciones básicas
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("La contraseña es obligatoria");
        }
        if (userService.existePorEmail(email)) {
            return ResponseEntity.badRequest().body("El email ya está registrado");
        }
        if (userService.existePorUsuario(usuario)) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsuario(usuario);
        user.setPassword(password);
        user.setRol(rol.toUpperCase());

        try {
            if ("CANDIDATO".equalsIgnoreCase(rol)) {
                // Extraer datos del objeto candidato
                Map<String, Object> candidato = (Map<String, Object>) request.get("candidato");
                String nombre = (String) candidato.get("nombre");
                String apellido = (String) candidato.get("apellido");

                System.out.println("Nombre: " + nombre);
                System.out.println("Apellido: " + apellido);

                if (nombre == null || apellido == null || nombre.isBlank() || apellido.isBlank()) {
                    return ResponseEntity.badRequest().body("Nombre y apellido son obligatorios para candidato");
                }

                candidatoService.registrarCandidato(user, nombre, apellido, null);
                System.out.println("Candidato creado exitosamente");

            } else if ("RECRUITER".equalsIgnoreCase(rol)) {
                Map<String, Object> empresa = (Map<String, Object>) request.get("empresa");
                String nombreEmpresa = (String) empresa.get("nombreEmpresa");
                String descripcion = (String) empresa.get("descripcion");
                String url = (String) empresa.get("url");

                if (nombreEmpresa == null || nombreEmpresa.isBlank()) {
                    return ResponseEntity.badRequest().body("Nombre de empresa es obligatorio para reclutador");
                }

                empresaService.registrarEmpresa(user, nombreEmpresa, descripcion, url, null);

            } else if ("ADMIN".equalsIgnoreCase(rol)) {
                Map<String, Object> admin = (Map<String, Object>) request.get("admin");
                String nombre = (String) admin.get("nombre");
                String apellido = (String) admin.get("apellido");

                if (nombre == null || apellido == null || nombre.isBlank() || apellido.isBlank()) {
                    return ResponseEntity.badRequest().body("Nombre y apellido son obligatorios para administrador");
                }

                user.setPassword(passwordEncoder.encode(user.getPassword()));

                Admin adminEntity = new Admin(nombre, apellido);

                adminEntity.setUsuario(user);
                user.setAdmin(adminEntity);

                userRepository.save(user);
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
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.get("email") != null)
            existingUser.setEmail((String) request.get("email"));
        if (request.get("usuario") != null)
            existingUser.setUsuario((String) request.get("usuario"));
        if (request.get("rol") != null)
            existingUser.setRol((String) request.get("rol"));
        if (request.get("password") != null && !((String) request.get("password")).isBlank()) {
            existingUser.setPassword(passwordEncoder.encode((String) request.get("password")));
        }

        String rol = existingUser.getRol();

        if ("CANDIDATO".equalsIgnoreCase(rol)) {
            Candidato candidato = existingUser.getCandidato();
            if (candidato == null) {
                candidato = new Candidato();
                candidato.setUsuario(existingUser);
                existingUser.setCandidato(candidato);
            }
            Map<String, Object> data = (Map<String, Object>) request.get("candidato");
            if (data != null) {
                if (data.get("nombre") != null)
                    candidato.setNombre((String) data.get("nombre"));
                if (data.get("apellido") != null)
                    candidato.setApellido((String) data.get("apellido"));
            }
        } else if ("RECRUITER".equalsIgnoreCase(rol)) {
            Empresa empresa = existingUser.getEmpresa();
            if (empresa == null) {
                empresa = new Empresa();
                empresa.setUsuario(existingUser);
                existingUser.setEmpresa(empresa);
            }
            Map<String, Object> data = (Map<String, Object>) request.get("empresa");
            if (data != null) {
                if (data.get("nombreEmpresa") != null)
                    empresa.setNombreEmpresa((String) data.get("nombreEmpresa"));
                if (data.get("descripcion") != null)
                    empresa.setDescripcion((String) data.get("descripcion"));
                if (data.get("url") != null)
                    empresa.setUrl((String) data.get("url"));
            }
        } else if ("ADMIN".equalsIgnoreCase(rol)) {
            Admin admin = existingUser.getAdmin();
            if (admin == null) {
                admin = new Admin();
                admin.setUsuario(existingUser);
                existingUser.setAdmin(admin);
            }
            Map<String, Object> data = (Map<String, Object>) request.get("admin");
            if (data != null) {
                if (data.get("nombre") != null)
                    admin.setNombre((String) data.get("nombre"));
                if (data.get("apellido") != null)
                    admin.setApellido((String) data.get("apellido"));
            }
        }

        User savedUser = userRepository.save(existingUser);

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

    @GetMapping("/empresas")
    public ResponseEntity<Page<Empresa>> getEmpresas(Pageable pageable) {
        return ResponseEntity.ok(empresaRepository.findAll(pageable));
    }

    @DeleteMapping("/empresas/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) {
        empresaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

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

    @GetMapping("/usuarios/{userId}/admin")
    public ResponseEntity<Admin> getAdminByUserId(@PathVariable Long userId) {
        return adminRepository.findByUsuarioId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuarios/{userId}/admin")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long userId, @RequestBody Admin adminData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Admin admin = user.getAdmin();
        if (admin == null) {
            admin = new Admin();
            admin.setUsuario(user);
            user.setAdmin(admin);
        }

        if (adminData.getNombre() != null)
            admin.setNombre(adminData.getNombre());
        if (adminData.getApellido() != null)
            admin.setApellido(adminData.getApellido());

        userRepository.save(user);
        return ResponseEntity.ok(user.getAdmin());
    }
}