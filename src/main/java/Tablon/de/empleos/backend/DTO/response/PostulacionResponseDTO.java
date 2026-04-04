package Tablon.de.empleos.backend.DTO.response;

import java.time.LocalDateTime;

public class PostulacionResponseDTO {
    private Long id;
    private LocalDateTime fechaPostulacion;
    private boolean estado;
    private String nombreCandidato;
    private String emailCandidato;
    private String tituloOferta;
    private String nombreEmpresa;

    public PostulacionResponseDTO() {
    }

    public PostulacionResponseDTO(Long id, LocalDateTime fechaPostulacion, boolean estado,
            String nombreCandidato, String emailCandidato,
            String tituloOferta, String nombreEmpresa) {
        this.id = id;
        this.fechaPostulacion = fechaPostulacion;
        this.estado = estado;
        this.nombreCandidato = nombreCandidato;
        this.emailCandidato = emailCandidato;
        this.tituloOferta = tituloOferta;
        this.nombreEmpresa = nombreEmpresa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(LocalDateTime fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getNombreCandidato() {
        return nombreCandidato;
    }

    public void setNombreCandidato(String nombreCandidato) {
        this.nombreCandidato = nombreCandidato;
    }

    public String getEmailCandidato() {
        return emailCandidato;
    }

    public void setEmailCandidato(String emailCandidato) {
        this.emailCandidato = emailCandidato;
    }

    public String getTituloOferta() {
        return tituloOferta;
    }

    public void setTituloOferta(String tituloOferta) {
        this.tituloOferta = tituloOferta;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }
}