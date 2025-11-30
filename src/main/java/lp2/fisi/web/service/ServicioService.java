package lp2.fisi.web.service;

import lp2.fisi.web.model.Servicio;
import lp2.fisi.web.model.SolicitudServicio; // <--- Asegúrate de tener este modelo creado
import lp2.fisi.web.repository.ServicioRepository;
import lp2.fisi.web.repository.SolicitudServicioRepository; // <--- Asegúrate de tener este repo creado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private SolicitudServicioRepository solicitudRepository; // Inyectamos el repositorio para guardar

    // Método para listar servicios en la web (solo los activos)
    public List<Servicio> obtenerServiciosActivos() {
        // Asumiendo que quieres mostrar todos por ahora. 
        // Si tienes un método específico en el repo como findByActivoTrue(), úsalo.
        return servicioRepository.findAll(); 
    }

    // Método para filtrar por categoría (pendiente de implementar lógica real si se requiere)
    public List<Servicio> obtenerServiciosPorCategoria(String categoria) {
        return servicioRepository.findAll(); 
    }

    public Servicio guardarServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    public Servicio obtenerServicioPorId(Integer id) {
        return servicioRepository.findById(id).orElse(null);
    }

    // MÉTODO ACTUALIZADO: Ahora guarda en la base de datos
    public void procesarSolicitud(Integer servicioId, String nombre, String dni, String telefono,
                                  String email, String dispositivo, String descripcion, String fechaCitaStr) {
        
        try {
            // 1. Convertir la fecha de String (que viene del formulario) a LocalDate
            LocalDate fechaCita = null;
            if (fechaCitaStr != null && !fechaCitaStr.isEmpty()) {
                fechaCita = LocalDate.parse(fechaCitaStr);
            }

            // 2. Crear la entidad con los datos
            SolicitudServicio solicitud = new SolicitudServicio(
                    servicioId, 
                    nombre, 
                    dni, 
                    telefono, 
                    email, 
                    dispositivo, 
                    descripcion, 
                    fechaCita
            );

            // 3. GUARDAR EN LA BASE DE DATOS
            solicitudRepository.save(solicitud);
            
            System.out.println("✅ Solicitud guardada correctamente para: " + nombre);

        } catch (Exception e) {
            System.err.println("❌ Error al guardar la solicitud: " + e.getMessage());
            e.printStackTrace();
            // Aquí podrías lanzar una excepción personalizada si quisieras mostrar el error al usuario
        }
    }
}