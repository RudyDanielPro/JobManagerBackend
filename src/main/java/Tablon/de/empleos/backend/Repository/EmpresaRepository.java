package Tablon.de.empleos.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Tablon.de.empleos.backend.Entity.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByUsuarioId(Long usuarioId);

    Optional<Empresa> findByNombre(String nombre);
    
}
