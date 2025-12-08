package lp.grupal.web.model;

// CORRECCIÓN: Usamos jakarta.persistence.Id en lugar de springframework...Id
import jakarta.persistence.Id; 

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idcategoria;

    private String nombre;
    private String descripcion;
    private Boolean activo;

    public Integer getIdcategoria() {
        return idcategoria;
    }
    public void setIdcategoria(Integer idcategoria) {
        this.idcategoria = idcategoria;
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
    public Boolean getActivo() {
        return activo;
    }
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    @Override
    public String toString() {
        return "Categoria [idcategoria=" + idcategoria + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", activo=" + activo + "]";
    }
}