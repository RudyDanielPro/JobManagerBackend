package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.Entity.OfertaLaboral;
import Tablon.de.empleos.backend.Repository.OfertaLaboralRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfertaLaboralService {

    private final OfertaLaboralRepository ofertaRepository;

    public OfertaLaboralService(OfertaLaboralRepository ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> obtenerOfertasActivas(Pageable pageable) {
        return ofertaRepository.findByEstadoTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> buscarPorTitulo(String titulo, Pageable pageable) {
        return ofertaRepository.findByTituloContainingIgnoreCaseAndEstadoTrue(titulo, pageable);
    }

    @Transactional
    public OfertaLaboral guardarOferta(OfertaLaboral oferta) {
        return ofertaRepository.save(oferta);
    }

    @Transactional
    public void eliminarOferta(Long id) {
        ofertaRepository.deleteById(id);
    }
}