package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.Entity.OfertaLaboral;
import Tablon.de.empleos.backend.Services.OfertaLaboralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "*")
public class OfertaLaboralController {

    private final OfertaLaboralService ofertaService;

    public OfertaLaboralController(OfertaLaboralService ofertaService) {
        this.ofertaService = ofertaService;
    }
    @GetMapping
    public ResponseEntity<Page<OfertaLaboral>> listarOfertas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        return ResponseEntity.ok(ofertaService.obtenerOfertasActivas(pageable));
    }

    // Buscar ofertas por título (también paginado)
    @GetMapping("/buscar")
    public ResponseEntity<Page<OfertaLaboral>> buscarOfertas(
            @RequestParam String titulo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ofertaService.buscarPorTitulo(titulo, pageable));
    }

    @PostMapping
    public ResponseEntity<OfertaLaboral> crearOferta(@RequestBody OfertaLaboral oferta) {
        return ResponseEntity.ok(ofertaService.guardarOferta(oferta));
    }
}