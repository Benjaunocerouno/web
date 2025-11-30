package lp2.fisi.web.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos_efectivo")
public class PagoEfectivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_efectivo")
    private Integer idEfectivo;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "direccion_entrega", columnDefinition = "TEXT")
    private String direccionEntrega;

    @Column(name = "estado", length = 20)
    private String estado = "pendiente";

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Constructores, getters y setters
    public PagoEfectivo() {}

    public PagoEfectivo(Integer idPedido, BigDecimal monto, String direccionEntrega) {
        this.idPedido = idPedido;
        this.monto = monto;
        this.direccionEntrega = direccionEntrega;
    }

    // Getters y Setters...
    public Integer getIdEfectivo() { return idEfectivo; }
    public void setIdEfectivo(Integer idEfectivo) { this.idEfectivo = idEfectivo; }
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}