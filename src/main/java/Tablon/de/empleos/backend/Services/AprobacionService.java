package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.DTO.response.AprobacionResponseDTO;
import Tablon.de.empleos.backend.Entity.*;
import Tablon.de.empleos.backend.Repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AprobacionService {

    private final TokenAprobacionRepository tokenRepository;
    private final PostulacionRepository postulacionRepository;
    private final OfertaLaboralRepository ofertaRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public AprobacionService(TokenAprobacionRepository tokenRepository,
                             PostulacionRepository postulacionRepository,
                             OfertaLaboralRepository ofertaRepository) {
        this.tokenRepository = tokenRepository;
        this.postulacionRepository = postulacionRepository;
        this.ofertaRepository = ofertaRepository;
    }

    public String generarTokenAprobacion(Long postulacionId) {
        tokenRepository.deleteByPostulacionId(postulacionId);
        
        String token = UUID.randomUUID().toString();
        TokenAprobacion tokenAprobacion = new TokenAprobacion(
            token,
            postulacionId,
            LocalDateTime.now().plusDays(7)
        );
        tokenRepository.save(tokenAprobacion);
        return token;
    }

    @Transactional
    public AprobacionResponseDTO aprobarPostulacion(String token) {
        TokenAprobacion tokenAprobacion = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        if (tokenAprobacion.isUsado()) {
            throw new RuntimeException("Este enlace ya ha sido utilizado");
        }

        if (tokenAprobacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace ha expirado");
        }

        Postulacion postulacion = postulacionRepository.findById(tokenAprobacion.getPostulacionId())
            .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));

        // Marcar postulación como aprobada
        postulacion.setEstado(true);
        postulacionRepository.save(postulacion);

        // Desactivar la oferta
        OfertaLaboral oferta = postulacion.getOfertaLaboral();
        oferta.setEstado(false);
        ofertaRepository.save(oferta);

        // Marcar token como usado
        tokenAprobacion.setUsado(true);
        tokenRepository.save(tokenAprobacion);

        return new AprobacionResponseDTO(
            "Postulación aprobada correctamente. La oferta ha sido cerrada.",
            true,
            postulacion.getCandidato().getNombre() + " " + postulacion.getCandidato().getApellido(),
            oferta.getTitulo()
        );
    }

    public String getUrlAprobacion(String token) {
        return baseUrl + "/api/aprobacion/confirmar?token=" + token;
    }
}