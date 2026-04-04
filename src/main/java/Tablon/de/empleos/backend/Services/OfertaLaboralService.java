package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.Entity.OfertaLaboral;
import Tablon.de.empleos.backend.Entity.Empresa;
import Tablon.de.empleos.backend.Repository.OfertaLaboralRepository;
import Tablon.de.empleos.backend.Repository.EmpresaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OfertaLaboralService {

    private final OfertaLaboralRepository ofertaRepository;
    private final EmpresaRepository empresaRepository;

    public OfertaLaboralService(OfertaLaboralRepository ofertaRepository, EmpresaRepository empresaRepository) {
        this.ofertaRepository = ofertaRepository;
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public OfertaLaboral publicarOferta(OfertaLaboral oferta, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));
        
        oferta.setEmpresa(empresa);
        oferta.setFechaCreacion(LocalDateTime.now());
        oferta.setEstado(true);
        
        return ofertaRepository.save(oferta);
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> obtenerOfertasActivas(Pageable pageable) {
        return ofertaRepository.findByEstadoTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> obtenerOfertasInactivas(Pageable pageable) {
        return ofertaRepository.findByEstadoFalse(pageable);
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> buscarPorTitulo(String titulo, Pageable pageable) {
        return ofertaRepository.findByTituloContainingIgnoreCaseAndEstadoTrue(titulo, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> buscarPorUbicacion(String ubicacion, Pageable pageable) {
        return ofertaRepository.findByUbicacionContainingIgnoreCaseAndEstadoTrue(ubicacion, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> buscarPorTituloYUbicacion(String titulo, String ubicacion, Pageable pageable) {
        return ofertaRepository.findByTituloContainingIgnoreCaseAndUbicacionContainingIgnoreCaseAndEstadoTrue(
            titulo, ubicacion, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> obtenerPorEmpresa(Long empresaId, Pageable pageable) {
        return ofertaRepository.findByEmpresaId(empresaId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OfertaLaboral> obtenerOfertasActivasPorEmpresa(Long empresaId, Pageable pageable) {
        return ofertaRepository.findByEmpresaIdAndEstadoTrue(empresaId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<OfertaLaboral> obtenerPorId(Long id) {
        return ofertaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<OfertaLaboral> obtenerPorIdConEmpresa(Long id) {
        return ofertaRepository.findByIdWithEmpresa(id);
    }

    @Transactional(readOnly = true)
    public List<OfertaLaboral> obtenerTodasConEmpresa() {
        return ofertaRepository.findAllWithEmpresa();
    }

    @Transactional(readOnly = true)
    public List<OfertaLaboral> obtenerPorEmpresaIdConEmpresa(Long empresaId) {
        return ofertaRepository.findByEmpresaIdWithEmpresa(empresaId);
    }

    @Transactional(readOnly = true)
    public long contarOfertasPorEmpresa(Long empresaId) {
        return ofertaRepository.countByEmpresaId(empresaId);
    }

    @Transactional(readOnly = true)
    public long contarOfertasActivasPorEmpresa(Long empresaId) {
        return ofertaRepository.countByEmpresaIdAndEstadoTrue(empresaId);
    }

    @Transactional(readOnly = true)
    public boolean existeOfertaEnEmpresa(Long empresaId) {
        return ofertaRepository.existsByEmpresaId(empresaId);
    }

    @Transactional
    public OfertaLaboral actualizarOferta(Long id, OfertaLaboral datosNuevos, Long empresaId) {
        OfertaLaboral oferta = ofertaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Oferta no encontrada con ID: " + id));

        if (!oferta.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Esta oferta no pertenece a la empresa especificada");
        }

        if (datosNuevos.getTitulo() != null) {
            oferta.setTitulo(datosNuevos.getTitulo());
        }
        if (datosNuevos.getDescripcion() != null) {
            oferta.setDescripcion(datosNuevos.getDescripcion());
        }
        if (datosNuevos.getUbicacion() != null) {
            oferta.setUbicacion(datosNuevos.getUbicacion());
        }
        if (datosNuevos.getRangoSalarial() != null) {
            oferta.setRangoSalarial(datosNuevos.getRangoSalarial());
        }

        return ofertaRepository.save(oferta);
    }

    @Transactional
    public OfertaLaboral activarOferta(Long id, Long empresaId) {
        OfertaLaboral oferta = ofertaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Oferta no encontrada con ID: " + id));
        
        if (!oferta.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Esta oferta no pertenece a la empresa especificada");
        }

        oferta.setEstado(true);
        return ofertaRepository.save(oferta);
    }

    @Transactional
    public OfertaLaboral desactivarOferta(Long id, Long empresaId) {
        OfertaLaboral oferta = ofertaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Oferta no encontrada con ID: " + id));
        
        if (!oferta.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Esta oferta no pertenece a la empresa especificada");
        }

        oferta.setEstado(false);
        return ofertaRepository.save(oferta);
    }

    @Transactional
    public void eliminarOferta(Long id, Long empresaId) {
        OfertaLaboral oferta = ofertaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Oferta no encontrada con ID: " + id));
        
        if (!oferta.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Esta oferta no pertenece a la empresa especificada");
        }

        ofertaRepository.deleteById(id);
    }
}