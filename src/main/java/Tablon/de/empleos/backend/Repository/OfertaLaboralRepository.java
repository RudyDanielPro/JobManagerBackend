package Tablon.de.empleos.backend.Repository;



import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import Tablon.de.empleos.backend.Entity.OfertaLaboral;

public interface OfertaLaboralRepository extends JpaRepository<OfertaLaboral, Long> {

    Page<OfertaLaboral> findByEmpresaId(Long empresaId,Pageable pageable);

    Page<OfertaLaboral> findByEstadoTrue(Pageable pageable);

    Page<OfertaLaboral> findByTituloContainingIgnoreCaseAndEstadoTrue(String titulo, Pageable pageable);
}
