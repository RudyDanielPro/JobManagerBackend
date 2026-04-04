package Tablon.de.empleos.backend.DTO.response;

public class EmpresaResponseDTO {
    private Long id;
    private String nombreEmpresa;
    private String descripcion;
    private String url;
    private String email;
    private String usuario;
    private String fotoUrl;

    public EmpresaResponseDTO() {
    }

    public EmpresaResponseDTO(Long id, String nombreEmpresa, String descripcion, String url,
            String email, String usuario, String fotoUrl) {
        this.id = id;
        this.nombreEmpresa = nombreEmpresa;
        this.descripcion = descripcion;
        this.url = url;
        this.email = email;
        this.usuario = usuario;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}