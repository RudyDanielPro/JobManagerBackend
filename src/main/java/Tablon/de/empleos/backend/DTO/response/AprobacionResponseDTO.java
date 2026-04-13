// DTO/response/AprobacionResponseDTO.java
package Tablon.de.empleos.backend.DTO.response;

public class AprobacionResponseDTO {
    private String mensaje;
    private boolean success;
    private String nombreCandidato;
    private String tituloOferta;

    public AprobacionResponseDTO() {
    }

    public AprobacionResponseDTO(String mensaje, boolean success, String nombreCandidato, String tituloOferta) {
        this.mensaje = mensaje;
        this.success = success;
        this.nombreCandidato = nombreCandidato;
        this.tituloOferta = tituloOferta;
    }

    // Getters y Setters
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getNombreCandidato() {
        return nombreCandidato;
    }

    public void setNombreCandidato(String nombreCandidato) {
        this.nombreCandidato = nombreCandidato;
    }

    public String getTituloOferta() {
        return tituloOferta;
    }

    public void setTituloOferta(String tituloOferta) {
        this.tituloOferta = tituloOferta;
    }
}