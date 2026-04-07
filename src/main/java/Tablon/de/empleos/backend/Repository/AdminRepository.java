package Tablon.de.empleos.backend.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import Tablon.de.empleos.backend.Entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsuarioId(Long userId);
}