package Tablon.de.empleos.backend.DTO.request;

import org.springframework.web.multipart.MultipartFile;

public class PostulacionRequestDTO {
    private Long ofertaId;
    private String mensaje;
    private MultipartFile cv;
    private String emailCandidato;
    private String nombre;
    private String apellido;

    public PostulacionRequestDTO() {
    }

    public Long getOfertaId() {
        return ofertaId;
    }

    public void setOfertaId(Long ofertaId) {
        this.ofertaId = ofertaId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public MultipartFile getCv() {
        return cv;
    }

    public void setCv(MultipartFile cv) {
        this.cv = cv;
    }

    public String getEmailCandidato() {
        return emailCandidato;
    }

    public void setEmailCandidato(String emailCandidato) {
        this.emailCandidato = emailCandidato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}