package lp.grupal.web.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Servicio;
import lp.grupal.web.model.SolicitudServicio;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.dao.IServicioDAO;
import lp.grupal.web.model.dao.ISolicitudServicioDAO;

@Controller
public class ServicioController {

    @Autowired
    private IServicioDAO servicioDAO;

    @Autowired
    private ISolicitudServicioDAO solicitudServicioDAO;

    // Listar servicios (Página Pública)
    @GetMapping("/servicio")
    public String servicio(Model model) {
        List<Servicio> servicios = servicioDAO.findByActivoTrue();
        model.addAttribute("listaServicios", servicios);
        return "servicio";
    }

    // Mostrar formulario de agendar (Requiere Login)
    @GetMapping("/servicio/agendar/{id}")
    public String mostrarFormularioAgendar(@PathVariable("id") Integer idServicio, HttpSession session, Model model, RedirectAttributes flash) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            flash.addFlashAttribute("error", "Debes iniciar sesión para agendar una cita.");
            return "redirect:/login";
        }

        Servicio servicio = servicioDAO.findById(idServicio).orElse(null);
        if (servicio == null) {
            return "redirect:/servicio";
        }

        model.addAttribute("servicio", servicio);
        return "cliente/agendar_servicio"; // Ruta dentro de templates/cliente
    }

    // Procesar el formulario
    @PostMapping("/servicio/guardar")
    public String guardarSolicitud(@RequestParam("idServicio") Integer idServicio,
                                   @RequestParam("dispositivo") String dispositivo,
                                   @RequestParam("problema") String problema,
                                   @RequestParam("fechaCita") String fechaCitaStr, // Recibimos como String del input datetime-local
                                   HttpSession session,
                                   RedirectAttributes flash) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        try {
            SolicitudServicio solicitud = new SolicitudServicio();
            solicitud.setUsuario(usuario);
            solicitud.setServicioBase(servicioDAO.findById(idServicio).get());
            solicitud.setDispositivoNombre(dispositivo);
            solicitud.setDescripcionProblema(problema);
            
            // Convertir String HTML datetime-local a LocalDateTime
            solicitud.setFechaCita(LocalDateTime.parse(fechaCitaStr)); 

            solicitudServicioDAO.save(solicitud);
            flash.addFlashAttribute("exito", "¡Cita agendada correctamente! Te contactaremos pronto.");
            
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al agendar: " + e.getMessage());
        }

        return "redirect:/servicio";
    }
}