package Tablon.de.empleos.backend.DTO;

import org.springframework.web.multipart.MultipartFile;

public record PostulacionRequest(
    Long ofertaId,
    String nombre,      
    String apellido,    
    String emailCandidato,
    String mensaje,
    String urlPortafolio,
    MultipartFile cv
) {}