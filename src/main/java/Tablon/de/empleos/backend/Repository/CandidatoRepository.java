package Tablon.de.empleos.backend.Repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Tablon.de.empleos.backend.Entity.Candidato;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    Page<Candidato> findByNombre(String nombre, Pageable pageable);

    Page<Candidato> findByApellido(String apellido, Pageable pageable);

    Page<Candidato> findByNombreAndApellido(String nombre, String apellido, Pageable pageable);
    
    List<Candidato> findByNombre(String nombre);

    List<Candidato> findByApellido(String apellido);

    List<Candidato> findByNombreAndApellido(String nombre, String apellido);
    
    Optional<Candidato> findByUsuarioId(Long userId);

    Optional<Candidato> findByUsuarioEmail(String email);

    Optional<Candidato> findByUsuario_Usuario(String nombreUsuario);
    
    @Query("SELECT c FROM Candidato c JOIN FETCH c.postulaciones")
    List<Candidato> findAllWithPostulaciones();
    
    @Query("SELECT c FROM Candidato c JOIN FETCH c.postulaciones WHERE c.id = :id")
    Optional<Candidato> findByIdWithPostulaciones(@Param("id") Long id);
    
    @Query("SELECT c FROM Candidato c JOIN FETCH c.postulaciones")
    Page<Candidato> findAllWithPostulaciones(Pageable pageable);
}