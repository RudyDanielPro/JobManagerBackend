package Tablon.de.empleos.backend.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "Empresa")
public class Empresa {

    @Id
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombreEmpresa;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonIgnore
    private User usuario;

    @Column(name = "pagina_web")
    private String url;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfertaLaboral> ofertas = new ArrayList<>();

    public Empresa() {
    }

    public Empresa(String nombre, String descripcion, String url) {
        this.nombreEmpresa = nombre;
        this.descripcion = descripcion;
        this.url = url;
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

    public List<OfertaLaboral> getOfertas() {
        return ofertas;
    }

    public void setOfertas(List<OfertaLaboral> ofertas) {
        this.ofertas = ofertas;
    }

    public void addOferta(OfertaLaboral oferta) {
        ofertas.add(oferta);
        oferta.setEmpresa(this);
    }

    public void removeOferta(OfertaLaboral oferta) {
        ofertas.remove(oferta);
        oferta.setEmpresa(null);
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    

}