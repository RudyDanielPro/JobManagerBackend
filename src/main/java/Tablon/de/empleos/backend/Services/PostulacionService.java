package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.DTO.PostulacionRequest;
import Tablon.de.empleos.backend.Entity.OfertaLaboral;
import Tablon.de.empleos.backend.Repository.OfertaLaboralRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostulacionService {

    private final OfertaLaboralRepository ofertaRepository;
    private final EmailService emailService;

    public PostulacionService(OfertaLaboralRepository ofertaRepository, EmailService emailService) {
        this.ofertaRepository = ofertaRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public void procesarPostulacion(PostulacionRequest request) {
        OfertaLaboral oferta = ofertaRepository.findById(request.ofertaId())
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada."));

        String emailDestino = oferta.getEmpresa().getCorreoContacto();
        String nombreCompleto = request.nombre() + " " + request.apellido();

        // Ahora enviamos también el email del candidato para el reply_to
        emailService.enviarCorreoResend(
            emailDestino, 
            oferta.getTitulo(),    
            nombreCompleto,
            request.emailCandidato(), // <--- Nuevo parámetro
            request.mensaje(),                
            request.cv()                    
        );
    }
}