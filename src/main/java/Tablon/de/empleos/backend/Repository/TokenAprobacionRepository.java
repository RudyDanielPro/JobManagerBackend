package Tablon.de.empleos.backend.Repository;

import Tablon.de.empleos.backend.Entity.TokenAprobacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenAprobacionRepository extends JpaRepository<TokenAprobacion, Long> {
    Optional<TokenAprobacion> findByToken(String token);

    void deleteByPostulacionId(Long postulacionId);
}