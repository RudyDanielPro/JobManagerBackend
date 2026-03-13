package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.Entity.Empresa;
import Tablon.de.empleos.backend.Repository.EmpresaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaRepository empresaRepository;

    public EmpresaController(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> obtenerEmpresa(@PathVariable Long id) {
        return empresaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizarEmpresa(@PathVariable Long id, @RequestBody Empresa datosActualizados) {
        return empresaRepository.findById(id)
                .map(empresa -> {
                    empresa.setNombre(datosActualizados.getNombre());
                    empresa.setDescripcion(datosActualizados.getDescripcion());
                    empresa.setCorreoContacto(datosActualizados.getCorreoContacto());
                    empresa.setUrl(datosActualizados.getUrl());
                    return ResponseEntity.ok(empresaRepository.save(empresa));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}