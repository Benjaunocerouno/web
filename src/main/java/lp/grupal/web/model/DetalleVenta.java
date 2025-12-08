package lp.grupal.web.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iddetalle;

    @ManyToOne
    @JoinColumn(name = "idventa")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "idproducto")
    private Producto producto;

    private Integer cantidad;
    
    @Column(name = "precio_unitario")
    private Double precioUnitario;
    
    // El subtotal se calcula en base de datos, pero podemos tenerlo aquí
    @Column(name = "subtotal", insertable = false, updatable = false)
    private Double subtotal;

    public Integer getIddetalle() {
        return iddetalle;
    }

    public void setIddetalle(Integer iddetalle) {
        this.iddetalle = iddetalle;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "DetalleVenta [iddetalle=" + iddetalle + ", venta=" + venta + ", producto=" + producto + ", cantidad="
                + cantidad + ", precioUnitario=" + precioUnitario + ", subtotal=" + subtotal + "]";
    }
}
