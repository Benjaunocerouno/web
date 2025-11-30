package lp2.fisi.web.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @Column(name = "id_cliente") // <--- NUEVO CAMPO
    private Integer idCliente;

    private String nombreCliente;
    private String email;
    private String telefono;
    private String direccion;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal montoTotal;
    
    private String estado = "PENDIENTE";
    private LocalDateTime fechaPedido = LocalDateTime.now();

    public Pedido() {}

    public Pedido(String nombre, String email, String telefono, String direccion, BigDecimal monto) {
        this.nombreCliente = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.montoTotal = monto;
    }

    // Getters y Setters
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public Integer getIdCliente() { return idCliente; } // <--- NUEVO GETTER
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; } // <--- NUEVO SETTER

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }
}