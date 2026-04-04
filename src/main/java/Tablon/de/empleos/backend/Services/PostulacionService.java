package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.DTO.request.PostulacionRequestDTO;
import Tablon.de.empleos.backend.Entity.Candidato;
import Tablon.de.empleos.backend.Entity.OfertaLaboral;
import Tablon.de.empleos.backend.Entity.Postulacion;
import Tablon.de.empleos.backend.Repository.CandidatoRepository;
import Tablon.de.empleos.backend.Repository.OfertaLaboralRepository;
import Tablon.de.empleos.backend.Repository.PostulacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostulacionService {

    private final PostulacionRepository postulacionRepository;
    private final OfertaLaboralRepository ofertaRepository;
    private final CandidatoRepository candidatoRepository;
    private final EmailService emailService;

    public PostulacionService(PostulacionRepository postulacionRepository,
            OfertaLaboralRepository ofertaRepository,
            CandidatoRepository candidatoRepository,
            EmailService emailService) {
        this.postulacionRepository = postulacionRepository;
        this.ofertaRepository = ofertaRepository;
        this.candidatoRepository = candidatoRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Postulacion crearPostulacion(PostulacionRequestDTO request, Long candidatoId) {

        OfertaLaboral oferta = ofertaRepository.findById(request.getOfertaId())
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada con ID: " + request.getOfertaId()));

        if (!oferta.isEstado()) {
            throw new RuntimeException("No puedes postularte a una oferta que no está activa");
        }

        Candidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado con ID: " + candidatoId));

        boolean yaExiste = postulacionRepository.existsByCandidatoIdAndOfertaId(candidatoId, request.getOfertaId());
        if (yaExiste) {
            throw new RuntimeException("Ya te has postulado a esta oferta anteriormente");
        }

        Postulacion postulacion = new Postulacion();
        postulacion.setFechaPostulacion(LocalDateTime.now());
        postulacion.setEstado(true);
        postulacion.setCandidato(candidato);
        postulacion.setOfertaLaboral(oferta);

        Postulacion nuevaPostulacion = postulacionRepository.save(postulacion);

        candidato.addPostulacion(nuevaPostulacion);

        String emailDestino = oferta.getEmpresa().getUsuario().getEmail();
        String nombreCompleto = candidato.getNombre() + " " + candidato.getApellido();

        emailService.enviarCorreoResend(
                emailDestino,
                oferta.getTitulo(),
                nombreCompleto,
                candidato.getUsuario().getEmail(),
                request.getMensaje(),
                request.getCv());

        return nuevaPostulacion;
    }

    @Transactional(readOnly = true)
    public Optional<Postulacion> buscarPorId(Long id) {
        return postulacionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Postulacion> buscarPorIdConDetalles(Long id) {
        return postulacionRepository.findByIdWithDetails(id);
    }

    @Transactional(readOnly = true)
    public Page<Postulacion> buscarPorCandidato(Long candidatoId, Pageable pageable) {
        return postulacionRepository.findByCandidatoId(candidatoId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Postulacion> buscarPorCandidato(Long candidatoId) {
        return postulacionRepository.findByCandidatoId(candidatoId);
    }

    @Transactional(readOnly = true)
    public List<Postulacion> buscarPorCandidatoConDetalles(Long candidatoId) {
        return postulacionRepository.findByCandidatoIdWithDetails(candidatoId);
    }

    @Transactional(readOnly = true)
    public Page<Postulacion> buscarPorOferta(Long ofertaId, Pageable pageable) {
        return postulacionRepository.findByOfertaId(ofertaId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Postulacion> buscarPorOferta(Long ofertaId) {
        return postulacionRepository.findByOfertaId(ofertaId);
    }

    @Transactional(readOnly = true)
    public List<Postulacion> buscarPorOfertaConDetalles(Long ofertaId) {
        return postulacionRepository.findByOfertaIdWithDetails(ofertaId);
    }

    @Transactional(readOnly = true)
    public long contarPostulacionesPorOferta(Long ofertaId) {
        return postulacionRepository.countByOfertaId(ofertaId);
    }

    @Transactional(readOnly = true)
    public long contarPostulacionesActivasPorOferta(Long ofertaId) {
        return postulacionRepository.countByOfertaIdAndEstado(ofertaId, true);
    }

    @Transactional(readOnly = true)
    public long contarPostulacionesPorCandidato(Long candidatoId) {
        return postulacionRepository.countByCandidatoId(candidatoId);
    }

    @Transactional(readOnly = true)
    public boolean yaSePostulo(Long candidatoId, Long ofertaId) {
        return postulacionRepository.existsByCandidatoIdAndOfertaId(candidatoId, ofertaId);
    }

    @Transactional(readOnly = true)
    public Page<Postulacion> buscarPorCandidatoYEstado(Long candidatoId, boolean estado, Pageable pageable) {
        return postulacionRepository.findByCandidatoIdAndEstado(candidatoId, estado, pageable);
    }

    @Transactional
    public Postulacion aceptarPostulacion(Long id, Long empresaId) {
        Postulacion postulacion = postulacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        if (!postulacion.getOfertaLaboral().getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("No tienes permisos para modificar esta postulación");
        }

        postulacion.setEstado(true);
        return postulacionRepository.save(postulacion);
    }

    @Transactional
    public Postulacion rechazarPostulacion(Long id, Long empresaId) {
        Postulacion postulacion = postulacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        if (!postulacion.getOfertaLaboral().getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("No tienes permisos para modificar esta postulación");
        }

        postulacion.setEstado(false); // Rechazada
        return postulacionRepository.save(postulacion);
    }

    @Transactional
    public void eliminarPostulacion(Long id, Long candidatoId) {
        Postulacion postulacion = postulacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        if (!postulacion.getCandidato().getId().equals(candidatoId)) {
            throw new RuntimeException("No tienes permisos para eliminar esta postulación");
        }

        postulacionRepository.deleteById(id);
    }
}