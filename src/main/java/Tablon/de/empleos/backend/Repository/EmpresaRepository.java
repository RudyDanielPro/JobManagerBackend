package Tablon.de.empleos.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Tablon.de.empleos.backend.Entity.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByNombreEmpresa(String nombreEmpresa);
    
    Page<Empresa> findByNombreEmpresaContaining(String keyword, Pageable pageable);

    List<Empresa> findByNombreEmpresaContaining(String keyword);
    
    Optional<Empresa> findByUsuarioId(Long userId);

    Optional<Empresa> findByUsuarioEmail(String email);
    
    @Query("SELECT e FROM Empresa e JOIN FETCH e.ofertas")
    List<Empresa> findAllWithOfertas();
    
    @Query("SELECT e FROM Empresa e JOIN FETCH e.ofertas WHERE e.id = :id")
    Optional<Empresa> findByIdWithOfertas(@Param("id") Long id);
    
    @Query("SELECT e FROM Empresa e JOIN FETCH e.ofertas")
    Page<Empresa> findAllWithOfertas(Pageable pageable);
}