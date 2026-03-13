package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.DTO.PostulacionRequest;
import Tablon.de.empleos.backend.Entity.OfertaLaboral;
import Tablon.de.empleos.backend.Repository.OfertaLaboralRepository;
import org.springframework.stereotype.Service;

@Service
public class PostulacionService {

    private final OfertaLaboralRepository ofertaRepository;
    private final EmailService emailService;

    public PostulacionService(OfertaLaboralRepository ofertaRepository, EmailService emailService) {
        this.ofertaRepository = ofertaRepository;
        this.emailService = emailService;
    }

    public void procesarPostulacion(PostulacionRequest request) {
    OfertaLaboral oferta = ofertaRepository.findById(request.ofertaId())
            .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

    String emailDestino = oferta.getEmpresa().getCorreoContacto();
    String nombreCompleto= request.nombre() + " " + request.apellido();

    emailService.enviarCorreoResend(
        emailDestino, 
        oferta.getTitulo(),    
        nombreCompleto,        
        request.mensaje(),               
        request.cv()                     
    );
}
}