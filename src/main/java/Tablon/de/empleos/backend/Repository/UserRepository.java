package Tablon.de.empleos.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Tablon.de.empleos.backend.Entity.User;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsuario(String usuario);

    boolean existsByEmail(String email);

    boolean existsByUsuario(String usuario);
}