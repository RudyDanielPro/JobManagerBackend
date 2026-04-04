package Tablon.de.empleos.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Tablon.de.empleos.backend.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsuario(String usuario);

    Optional<User> findByEmailOrUsuario(String email, String usuario);
    
    List<User> findByRol(String rol);

    Page<User> findByRol(String rol, Pageable pageable);
    
    boolean existsByEmail(String email);

    boolean existsByUsuario(String usuario);
    
    @Query("SELECT u FROM User u JOIN FETCH u.candidato WHERE u.id = :id AND u.rol = 'candidato'")
    Optional<User> findByIdWithCandidato(@Param("id") Long id);
    
    @Query("SELECT u FROM User u JOIN FETCH u.empresa WHERE u.id = :id AND u.rol = 'reclutador'")
    Optional<User> findByIdWithEmpresa(@Param("id") Long id);
    
    @Query("SELECT u FROM User u JOIN FETCH u.candidato WHERE u.email = :email AND u.rol = 'candidato'")
    Optional<User> findByEmailWithCandidato(@Param("email") String email);
    
    @Query("SELECT u FROM User u JOIN FETCH u.empresa WHERE u.email = :email AND u.rol = 'reclutador'")
    Optional<User> findByEmailWithEmpresa(@Param("email") String email);
}