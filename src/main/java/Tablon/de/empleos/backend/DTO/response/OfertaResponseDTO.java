package Tablon.de.empleos.backend.DTO.response;

import java.time.LocalDateTime;

public class OfertaResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private String rangoSalarial;
    private LocalDateTime fechaCreacion;
    private boolean estado;
    private String nombreEmpresa;
    private Long empresaId;

    public OfertaResponseDTO() {
    }

    public OfertaResponseDTO(Long id, String titulo, String descripcion, String ubicacion,
            String rangoSalarial, LocalDateTime fechaCreacion, boolean estado,
            String nombreEmpresa, Long empresaId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.rangoSalarial = rangoSalarial;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.nombreEmpresa = nombreEmpresa;
        this.empresaId = empresaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getRangoSalarial() {
        return rangoSalarial;
    }

    public void setRangoSalarial(String rangoSalarial) {
        this.rangoSalarial = rangoSalarial;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }
}