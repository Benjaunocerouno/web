package lp2.fisi.web.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_servicio")
public class SolicitudServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_cliente") // <--- NUEVO CAMPO
    private Integer idCliente;

    private Integer servicioId;
    private String nombreCliente;
    private String dni;
    private String telefono;
    private String email;
    private String dispositivo;
    private String descripcionProblema;
    private LocalDate fechaCita;
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public SolicitudServicio() {}

    // Constructor actualizado para incluir idCliente (opcional, o usar setters)
    public SolicitudServicio(Integer servicioId, String nombre, String dni, String telefono, 
                             String email, String dispositivo, String descripcion, LocalDate fechaCita) {
        this.servicioId = servicioId;
        this.nombreCliente = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.dispositivo = dispositivo;
        this.descripcionProblema = descripcion;
        this.fechaCita = fechaCita;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public Integer getServicioId() { return servicioId; }
    public void setServicioId(Integer servicioId) { this.servicioId = servicioId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDispositivo() { return dispositivo; }
    public void setDispositivo(String dispositivo) { this.dispositivo = dispositivo; }

    public String getDescripcionProblema() { return descripcionProblema; }
    public void setDescripcionProblema(String descripcionProblema) { this.descripcionProblema = descripcionProblema; }

    public LocalDate getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDate fechaCita) { this.fechaCita = fechaCita; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}