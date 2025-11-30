package lp2.fisi.web.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos_yape")
public class PagoYape {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_yape")
    private Integer idYape;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "numero_celular", length = 20)
    private String numeroCelular;

    @Column(name = "nombre_remitente", length = 100)
    private String nombreRemitente;

    @Column(name = "dni_remitente", length = 8)
    private String dniRemitente;

    @Column(name = "comprobante_url", length = 255)
    private String comprobanteUrl;

    @Column(name = "estado", length = 20)
    private String estado = "pendiente";

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Constructores, getters y setters
    public PagoYape() {}

    public PagoYape(Integer idPedido, BigDecimal monto, String numeroCelular, String nombreRemitente) {
        this.idPedido = idPedido;
        this.monto = monto;
        this.numeroCelular = numeroCelular;
        this.nombreRemitente = nombreRemitente;
    }

    // Getters y Setters...
    public Integer getIdYape() { return idYape; }
    public void setIdYape(Integer idYape) { this.idYape = idYape; }
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getNumeroCelular() { return numeroCelular; }
    public void setNumeroCelular(String numeroCelular) { this.numeroCelular = numeroCelular; }
    public String getNombreRemitente() { return nombreRemitente; }
    public void setNombreRemitente(String nombreRemitente) { this.nombreRemitente = nombreRemitente; }
    public String getDniRemitente() { return dniRemitente; }
    public void setDniRemitente(String dniRemitente) { this.dniRemitente = dniRemitente; }
    public String getComprobanteUrl() { return comprobanteUrl; }
    public void setComprobanteUrl(String comprobanteUrl) { this.comprobanteUrl = comprobanteUrl; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}