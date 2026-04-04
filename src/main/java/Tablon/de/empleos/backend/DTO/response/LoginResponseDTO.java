package Tablon.de.empleos.backend.DTO.response;

public class LoginResponseDTO {
    private String token;
    private String rol;
    private Long id;
    private String email;
    private String usuario;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String rol, Long id, String email, String usuario) {
        this.token = token;
        this.rol = rol;
        this.id = id;
        this.email = email;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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
}