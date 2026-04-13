// Entity/TokenAprobacion.java
package Tablon.de.empleos.backend.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_aprobacion")
public class TokenAprobacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "postulacion_id", nullable = false)
    private Long postulacionId;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "usado", nullable = false)
    private boolean usado = false;

    public TokenAprobacion() {
    }

    public TokenAprobacion(String token, Long postulacionId, LocalDateTime fechaExpiracion) {
        this.token = token;
        this.postulacionId = postulacionId;
        this.fechaExpiracion = fechaExpiracion;
        this.usado = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getPostulacionId() {
        return postulacionId;
    }

    public void setPostulacionId(Long postulacionId) {
        this.postulacionId = postulacionId;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}