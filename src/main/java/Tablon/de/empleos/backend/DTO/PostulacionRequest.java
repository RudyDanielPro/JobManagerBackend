package Tablon.de.empleos.backend.DTO;

import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostulacionRequest {
    private Long ofertaId;
    private String nombre;      
    private String apellido;    
    private String emailCandidato;
    private String mensaje;
    private String urlPortafolio;
    private MultipartFile cv;
}   