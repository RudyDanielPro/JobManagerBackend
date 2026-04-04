package Tablon.de.empleos.backend.DTO.request;

public class OfertaRequestDTO {
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private String rangoSalarial;

    public OfertaRequestDTO() {
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
}