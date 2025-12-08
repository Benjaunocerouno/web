package lp.grupal.web.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "venta")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idventa;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Usuario usuario; 

    private LocalDateTime fechaventa;
    private String estado; 
    private Double monto_total;
    private String direccion_envio;
    private String ciudad_envio;
    private String referencia_envio;
    private String telefono_contacto;

    // --- NUEVOS CAMPOS AGREGADOS ---
    // (Deben coincidir con los nombres en tu base de datos)
    private String tipo_comprobante;
    private String serie_comprobante;
    private String num_comprobante;

    // Relación con los detalles
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;

    @PrePersist
    public void prePersist() {
        fechaventa = LocalDateTime.now();
        // Valor por defecto si no se asigna antes
        if (estado == null) estado = "PENDIENTE";
        if (tipo_comprobante == null) tipo_comprobante = "BOLETA";
    }

    @ManyToOne
    @JoinColumn(name = "id_empleado") // Relación con la columna de tu BD
    private Usuario empleado;

    // --- GETTERS Y SETTERS ---

    public Integer getIdventa() {
        return idventa;
    }

    public void setIdventa(Integer idventa) {
        this.idventa = idventa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaventa() {
        return fechaventa;
    }

    public void setFechaventa(LocalDateTime fechaventa) {
        this.fechaventa = fechaventa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getMonto_total() {
        return monto_total;
    }

    public void setMonto_total(Double monto_total) {
        this.monto_total = monto_total;
    }

    public String getDireccion_envio() {
        return direccion_envio;
    }

    public void setDireccion_envio(String direccion_envio) {
        this.direccion_envio = direccion_envio;
    }

    public String getCiudad_envio() {
        return ciudad_envio;
    }

    public void setCiudad_envio(String ciudad_envio) {
        this.ciudad_envio = ciudad_envio;
    }

    public String getReferencia_envio() {
        return referencia_envio;
    }

    public void setReferencia_envio(String referencia_envio) {
        this.referencia_envio = referencia_envio;
    }

    public String getTelefono_contacto() {
        return telefono_contacto;
    }

    public void setTelefono_contacto(String telefono_contacto) {
        this.telefono_contacto = telefono_contacto;
    }

    // --- GETTERS Y SETTERS NUEVOS ---
    public String getTipo_comprobante() {
        return tipo_comprobante;
    }

    public void setTipo_comprobante(String tipo_comprobante) {
        this.tipo_comprobante = tipo_comprobante;
    }

    public String getSerie_comprobante() {
        return serie_comprobante;
    }

    public void setSerie_comprobante(String serie_comprobante) {
        this.serie_comprobante = serie_comprobante;
    }

    public String getNum_comprobante() {
        return num_comprobante;
    }

    public void setNum_comprobante(String num_comprobante) {
        this.num_comprobante = num_comprobante;
    }
    // --------------------------------

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public Usuario getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Usuario empleado) {
        this.empleado = empleado;
    }

    @Override
    public String toString() {
        return "Venta [idventa=" + idventa + ", usuario=" + usuario + ", fechaventa=" + fechaventa + ", estado="
                + estado + ", monto_total=" + monto_total + ", tipo_comprobante=" + tipo_comprobante + "]";
    }
}