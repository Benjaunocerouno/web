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

    private String nombreCliente;
    private String email;
    private String telefono;
    private String direccion;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal montoTotal;
    
    private String estado = "PENDIENTE";
    private LocalDateTime fechaPedido = LocalDateTime.now();

    // Constructores
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
    // ... resto de getters y setters ...
}