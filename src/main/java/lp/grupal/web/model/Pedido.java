package lp.grupal.web.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idpedido;

    // Relación con el cliente (Usuario)
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Usuario usuario;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    private String estado; // 'PENDIENTE', etc.

    @Column(name = "monto_estimado")
    private Double montoEstimado;

    @Column(name = "observaciones_cliente")
    private String observacionesCliente;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;
    
    // Estos pueden ser nulos al inicio
    @Column(name = "id_vendedor_revisor")
    private Integer idVendedorRevisor;

    @Column(name = "id_venta_generada")
    private Integer idVentaGenerada;

    // Relación con los detalles del pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;

    @PrePersist
    public void prePersist() {
        fechaPedido = LocalDateTime.now();
        estado = "PENDIENTE";
    }

    // --- GETTERS Y SETTERS ---
    public Integer getIdpedido() { return idpedido; }
    public void setIdpedido(Integer idpedido) { this.idpedido = idpedido; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getMontoEstimado() { return montoEstimado; }
    public void setMontoEstimado(Double montoEstimado) { this.montoEstimado = montoEstimado; }

    public String getObservacionesCliente() { return observacionesCliente; }
    public void setObservacionesCliente(String observacionesCliente) { this.observacionesCliente = observacionesCliente; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public Integer getIdVendedorRevisor() { return idVendedorRevisor; }
    public void setIdVendedorRevisor(Integer idVendedorRevisor) { this.idVendedorRevisor = idVendedorRevisor; }

    public Integer getIdVentaGenerada() { return idVentaGenerada; }
    public void setIdVentaGenerada(Integer idVentaGenerada) { this.idVentaGenerada = idVentaGenerada; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }
}