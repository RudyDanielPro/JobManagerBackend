package Tablon.de.empleos.backend.Repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Tablon.de.empleos.backend.Entity.UserFoto;

public interface UserFotoRepository extends JpaRepository<UserFoto, Long> {

    Optional<UserFoto> findByRuta(String ruta);
    
    List<UserFoto> findByNombreArchivo(String nombreArchivo);
}