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

    private Integer servicioId;
    private String nombreCliente;
    private String dni;
    private String telefono;
    private String email;
    private String dispositivo;
    private String descripcionProblema;
    private LocalDate fechaCita;
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Constructores
    public SolicitudServicio() {}

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

    // Getters y Setters (Omitidos por brevedad, pero necesarios)
    // ... genera los getters y setters aquí ...
}