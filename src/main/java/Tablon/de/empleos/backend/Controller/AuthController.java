package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.request.LoginRequestDTO;
import Tablon.de.empleos.backend.DTO.request.UserRegistroRequestDTO;
import Tablon.de.empleos.backend.DTO.response.LoginResponseDTO;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Security.jwt.JwtService;
import Tablon.de.empleos.backend.Services.CandidatoService;
import Tablon.de.empleos.backend.Services.EmpresaService;
import Tablon.de.empleos.backend.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final UserService userService;
    private final CandidatoService candidatoService;
    private final EmpresaService empresaService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService,
            CandidatoService candidatoService,
            EmpresaService empresaService,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userService = userService;
        this.candidatoService = candidatoService;
        this.empresaService = empresaService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/registro", consumes = { "multipart/form-data" })
    public ResponseEntity<?> registrar(
            @RequestPart("usuario") String usuarioJson,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRegistroRequestDTO request = objectMapper.readValue(usuarioJson, UserRegistroRequestDTO.class);
            request.setFoto(archivo);

            // Verificar si ya existe
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
            user.setPassword(request.getPassword());
            user.setRol(request.getRol());

            if ("candidato".equalsIgnoreCase(request.getRol())) {
                candidatoService.registrarCandidato(user, request.getNombre(), request.getApellido(), archivo);
            } else if ("reclutador".equalsIgnoreCase(request.getRol())) {
                empresaService.registrarEmpresa(user, request.getNombreEmpresa(),
                        request.getDescripcion(), request.getUrl(), archivo);
            } else {
                return ResponseEntity.badRequest().body("Rol no válido");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO credentials) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getIdentificador(),
                            credentials.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            User user = userService.buscarPorEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String rol = user.getRol().replace("ROLE_", "").toLowerCase();

            return ResponseEntity
                    .ok(new LoginResponseDTO(token, rol, user.getId(), user.getEmail(), user.getUsuario()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
        }
    }
}