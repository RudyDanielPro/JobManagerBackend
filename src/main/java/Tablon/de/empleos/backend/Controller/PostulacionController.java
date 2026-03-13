package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.PostulacionRequest;
import Tablon.de.empleos.backend.Services.PostulacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/postulaciones")
@CrossOrigin(origins = "*") // Permite peticiones desde tu React
public class PostulacionController {

    private final PostulacionService postulacionService;

    public PostulacionController(PostulacionService postulacionService) {
        this.postulacionService = postulacionService;
    }

    @PostMapping(value = "/enviar", consumes = {"multipart/form-data"})
    public ResponseEntity<String> enviarPostulacion(@ModelAttribute PostulacionRequest request) {
        try {
            postulacionService.procesarPostulacion(request);
            return ResponseEntity.ok("Postulación enviada con éxito al reclutador.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la postulación: " + e.getMessage());
        }
    }
}