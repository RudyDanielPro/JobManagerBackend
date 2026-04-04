package Tablon.de.empleos.backend.Repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Tablon.de.empleos.backend.Entity.OfertaLaboral;

public interface OfertaLaboralRepository extends JpaRepository<OfertaLaboral, Long> {

    Page<OfertaLaboral> findByEmpresaId(Long empresaId, Pageable pageable);

    List<OfertaLaboral> findByEmpresaId(Long empresaId);
    

    Page<OfertaLaboral> findByEstadoTrue(Pageable pageable);

    List<OfertaLaboral> findByEstadoTrue();

    Page<OfertaLaboral> findByEstadoFalse(Pageable pageable);

    List<OfertaLaboral> findByEstadoFalse();
    
    Page<OfertaLaboral> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    Page<OfertaLaboral> findByTituloContainingIgnoreCaseAndEstadoTrue(String titulo, Pageable pageable);

    List<OfertaLaboral> findByTituloContainingIgnoreCase(String titulo);
    
    Page<OfertaLaboral> findByUbicacionContainingIgnoreCase(String ubicacion, Pageable pageable);

    Page<OfertaLaboral> findByUbicacionContainingIgnoreCaseAndEstadoTrue(String ubicacion, Pageable pageable);
    
    Page<OfertaLaboral> findByTituloContainingIgnoreCaseAndUbicacionContainingIgnoreCaseAndEstadoTrue(
        String titulo, String ubicacion, Pageable pageable);
    
    Page<OfertaLaboral> findByEmpresaIdAndEstadoTrue(Long empresaId, Pageable pageable);
    
    @Query("SELECT o FROM OfertaLaboral o JOIN FETCH o.empresa")
    List<OfertaLaboral> findAllWithEmpresa();
    
    @Query("SELECT o FROM OfertaLaboral o JOIN FETCH o.empresa WHERE o.id = :id")
    Optional<OfertaLaboral> findByIdWithEmpresa(@Param("id") Long id);
    
    @Query("SELECT o FROM OfertaLaboral o JOIN FETCH o.empresa")
    Page<OfertaLaboral> findAllWithEmpresa(Pageable pageable);
    
    @Query("SELECT o FROM OfertaLaboral o JOIN FETCH o.empresa WHERE o.empresa.id = :empresaId")
    List<OfertaLaboral> findByEmpresaIdWithEmpresa(@Param("empresaId") Long empresaId);
    
    long countByEmpresaId(Long empresaId);

    long countByEmpresaIdAndEstadoTrue(Long empresaId);

    boolean existsByEmpresaId(Long empresaId);
}