package Tablon.de.empleos.backend.Repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Tablon.de.empleos.backend.Entity.Postulacion;

public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    Page<Postulacion> findByCandidatoId(Long candidatoId, Pageable pageable);

    List<Postulacion> findByCandidatoId(Long candidatoId);

    Page<Postulacion> findByCandidatoIdOrderByFechaPostulacionDesc(Long candidatoId, Pageable pageable);

    List<Postulacion> findByCandidatoIdOrderByFechaPostulacionDesc(Long candidatoId);

    Page<Postulacion> findByOfertaId(Long ofertaId, Pageable pageable);

    List<Postulacion> findByOfertaId(Long ofertaId);

    Page<Postulacion> findByOfertaIdOrderByFechaPostulacionDesc(Long ofertaId, Pageable pageable);

    List<Postulacion> findByOfertaIdOrderByFechaPostulacionDesc(Long ofertaId);

    Page<Postulacion> findByEstado(boolean estado, Pageable pageable);

    List<Postulacion> findByEstado(boolean estado);

    Optional<Postulacion> findByCandidatoIdAndOfertaId(Long candidatoId, Long ofertaId);

    boolean existsByCandidatoIdAndOfertaId(Long candidatoId, Long ofertaId);

    long countByOfertaId(Long ofertaId);

    long countByOfertaIdAndEstado(Long ofertaId, boolean estado);

    long countByCandidatoId(Long candidatoId);

    @Query("SELECT p FROM Postulacion p JOIN FETCH p.candidato JOIN FETCH p.ofertaLaboral WHERE p.id = :id")
    Optional<Postulacion> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM Postulacion p JOIN FETCH p.candidato JOIN FETCH p.ofertaLaboral")
    List<Postulacion> findAllWithDetails();

    @Query("SELECT p FROM Postulacion p JOIN FETCH p.candidato JOIN FETCH p.ofertaLaboral WHERE p.candidato.id = :candidatoId")
    List<Postulacion> findByCandidatoIdWithDetails(@Param("candidatoId") Long candidatoId);

    @Query("SELECT p FROM Postulacion p JOIN FETCH p.candidato JOIN FETCH p.ofertaLaboral WHERE p.ofertaLaboral.id = :ofertaId")
    List<Postulacion> findByOfertaIdWithDetails(@Param("ofertaId") Long ofertaId);

    Page<Postulacion> findByCandidatoIdAndEstado(Long candidatoId, boolean estado, Pageable pageable);
}