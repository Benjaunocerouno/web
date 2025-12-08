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
    private Usuario usuario; // El cliente que compró

    private LocalDateTime fechaventa;
    private String estado; // PENDIENTE, PAGADO, ETC.
    private Double monto_total;
    private String direccion_envio;
    private String ciudad_envio;
    private String referencia_envio;
    private String telefono_contacto;

    // Relación para guardar los detalles automáticamente
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;

    @PrePersist
    public void prePersist() {
        fechaventa = LocalDateTime.now();
        estado = "PENDIENTE";
    }

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

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
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

    @Override
    public String toString() {
        return "Venta [idventa=" + idventa + ", usuario=" + usuario + ", fechaventa=" + fechaventa + ", estado="
                + estado + ", monto_total=" + monto_total + ", direccion_envio=" + direccion_envio + ", ciudad_envio="
                + ciudad_envio + ", referencia_envio=" + referencia_envio + ", telefono_contacto=" + telefono_contacto
                + ", detalles=" + detalles + "]";
    }
}
