package Tablon.de.empleos.backend.DTO.request;

public class EmpresaUpdateRequestDTO {
    private String nombreEmpresa;
    private String descripcion;
    private String url;
    private String email;
    
    public EmpresaUpdateRequestDTO() {}
 
    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}