package Tablon.de.empleos.backend.DTO.response;

public class UserResponseDTO {
    private Long id;
    private String email;
    private String usuario;
    private String rol;
    private String fotoUrl;

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String email, String usuario, String rol, String fotoUrl) {
        this.id = id;
        this.email = email;
        this.usuario = usuario;
        this.rol = rol;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}