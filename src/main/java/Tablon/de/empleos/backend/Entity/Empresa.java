package Tablon.de.empleos.backend.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "correo_contacto", nullable = false)
    private String correoContacto; // Este es el que usaremos para Resend

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "foto_id")
    private UserFoto foto;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @Column(name = "pagina_web")
    private String url;

    public Empresa() {
    }

    public Empresa(String nombre, String descripcion, String correoContacto, UserFoto foto, User usuario, String url) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.correoContacto = correoContacto;
        this.foto = foto;
        this.usuario = usuario;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(String correoContacto) {
        this.correoContacto = correoContacto;
    }

    public UserFoto getFoto() {
        return foto;
    }

    public void setFoto(UserFoto foto) {
        this.foto = foto;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    
}