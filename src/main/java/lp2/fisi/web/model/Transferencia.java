package lp2.fisi.web.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "transferencias")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transferencia")
    private Integer idTransferencia;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "banco_origen", nullable = false, length = 50)
    private String bancoOrigen;

    @Column(name = "banco_destino", nullable = false, length = 50)
    private String bancoDestino;

    @Column(name = "numero_operacion", length = 50)
    private String numeroOperacion;

    @Column(name = "fecha_transferencia")
    private LocalDate fechaTransferencia;

    @Column(name = "hora_transferencia")
    private LocalTime horaTransferencia;

    @Column(name = "nombre_remitente", length = 100)
    private String nombreRemitente;

    @Column(name = "dni_remitente", length = 8)
    private String dniRemitente;

    @Column(name = "comprobante_url", length = 255)
    private String comprobanteUrl;

    @Column(name = "estado", length = 20)
    private String estado = "pendiente";

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    @Column(name = "id_usuario_verifica")
    private Integer idUsuarioVerifica;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    // Constructores
    public Transferencia() {}

    public Transferencia(Integer idPedido, BigDecimal monto, String bancoOrigen,
                         String bancoDestino, String nombreRemitente, String dniRemitente) {
        this.idPedido = idPedido;
        this.monto = monto;
        this.bancoOrigen = bancoOrigen;
        this.bancoDestino = bancoDestino;
        this.nombreRemitente = nombreRemitente;
        this.dniRemitente = dniRemitente;
    }

    // Getters y Setters
    public Integer getIdTransferencia() { return idTransferencia; }
    public void setIdTransferencia(Integer idTransferencia) { this.idTransferencia = idTransferencia; }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getBancoOrigen() { return bancoOrigen; }
    public void setBancoOrigen(String bancoOrigen) { this.bancoOrigen = bancoOrigen; }

    public String getBancoDestino() { return bancoDestino; }
    public void setBancoDestino(String bancoDestino) { this.bancoDestino = bancoDestino; }

    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }

    public LocalDate getFechaTransferencia() { return fechaTransferencia; }
    public void setFechaTransferencia(LocalDate fechaTransferencia) { this.fechaTransferencia = fechaTransferencia; }

    public LocalTime getHoraTransferencia() { return horaTransferencia; }
    public void setHoraTransferencia(LocalTime horaTransferencia) { this.horaTransferencia = horaTransferencia; }

    public String getNombreRemitente() { return nombreRemitente; }
    public void setNombreRemitente(String nombreRemitente) { this.nombreRemitente = nombreRemitente; }

    public String getDniRemitente() { return dniRemitente; }
    public void setDniRemitente(String dniRemitente) { this.dniRemitente = dniRemitente; }

    public String getComprobanteUrl() { return comprobanteUrl; }
    public void setComprobanteUrl(String comprobanteUrl) { this.comprobanteUrl = comprobanteUrl; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaVerificacion() { return fechaVerificacion; }
    public void setFechaVerificacion(LocalDateTime fechaVerificacion) { this.fechaVerificacion = fechaVerificacion; }

    public Integer getIdUsuarioVerifica() { return idUsuarioVerifica; }
    public void setIdUsuarioVerifica(Integer idUsuarioVerifica) { this.idUsuarioVerifica = idUsuarioVerifica; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}