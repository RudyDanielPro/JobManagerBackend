package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.request.EmpresaUpdateRequestDTO;
import Tablon.de.empleos.backend.DTO.response.EmpresaResponseDTO;
import Tablon.de.empleos.backend.Entity.Empresa;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Services.EmpresaService;
import Tablon.de.empleos.backend.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/empresas")

public class EmpresaController {

    private final EmpresaService empresaService;
    private final UserService userService;

    public EmpresaController(EmpresaService empresaService, UserService userService) {
        this.empresaService = empresaService;
        this.userService = userService;
    }

    private EmpresaResponseDTO toDTO(Empresa empresa) {
        String fotoUrl = empresa.getUsuario().getFoto() != null ? empresa.getUsuario().getFoto().getRuta() : null;
        return new EmpresaResponseDTO(
                empresa.getId(),
                empresa.getNombreEmpresa(),
                empresa.getDescripcion(),
                empresa.getUrl(),
                empresa.getUsuario().getEmail(),
                empresa.getUsuario().getUsuario(),
                fotoUrl);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> obtenerEmpresa(@PathVariable Long id) {
        return empresaService.buscarPorId(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mi-perfil")
    public ResponseEntity<EmpresaResponseDTO> miPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return empresaService.buscarPorUsuarioId(user.getId())
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<EmpresaResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(empresaService.buscarTodosPaginado(pageable).map(this::toDTO));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<EmpresaResponseDTO>> buscarPorNombre(
            @RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(empresaService.buscarPorNombreContaining(keyword, pageable).map(this::toDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> actualizarEmpresa(
            @PathVariable Long id,
            @RequestBody EmpresaUpdateRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User usuarioAutenticado = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Empresa empresa = empresaService.actualizarPerfil(
                id, request.getNombreEmpresa(), request.getDescripcion(), request.getUrl(), usuarioAutenticado);

        if (request.getEmail() != null) {
            empresaService.actualizarEmailContacto(id, request.getEmail(), usuarioAutenticado);
        }

        return ResponseEntity.ok(toDTO(empresa));
    }

    @PutMapping("/{id}/logo")
    public ResponseEntity<EmpresaResponseDTO> actualizarLogo(
            @PathVariable Long id,
            @RequestParam("logo") MultipartFile logo,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        User usuarioAutenticado = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Empresa empresa = empresaService.actualizarLogo(id, logo, usuarioAutenticado);
        return ResponseEntity.ok(toDTO(empresa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpresa(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User usuarioAutenticado = userService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        empresaService.eliminarEmpresa(id, usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }
}