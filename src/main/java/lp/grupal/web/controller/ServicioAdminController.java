package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.Servicio;
import lp.grupal.web.model.dao.IServicioDAO;

import java.util.List;

@Controller
@RequestMapping("/admin/servicios")
public class ServicioAdminController {

    @Autowired private IServicioDAO servicioDAO;

    // LISTAR
    @GetMapping
    public String listar(Model model, @RequestParam(value = "buscar", required = false) String buscar, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        List<Servicio> servicios;
        if (buscar != null && !buscar.isEmpty()) {
            servicios = servicioDAO.buscarPorCriterio(buscar);
        } else {
            servicios = servicioDAO.findByActivoTrue();
        }

        model.addAttribute("servicios", servicios);
        model.addAttribute("palabraClave", buscar);
        return "empresa/servicios_admin"; // HTML de la lista
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        model.addAttribute("servicio", new Servicio());
        model.addAttribute("titulo", "Nuevo Servicio");
        return "empresa/form_servicio"; // HTML del formulario
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model, HttpSession session, RedirectAttributes flash) {
        if (!validarAcceso(session)) return "redirect:/login";

        Servicio serv = servicioDAO.findById(id).orElse(null);
        if (serv == null) {
            flash.addFlashAttribute("error", "Servicio no encontrado.");
            return "redirect:/admin/servicios";
        }

        model.addAttribute("servicio", serv);
        model.addAttribute("titulo", "Editar Servicio");
        return "empresa/form_servicio";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Servicio servicio, RedirectAttributes flash, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        try {
            if(servicio.getActivo() == null) servicio.setActivo(true);
            servicioDAO.save(servicio);
            flash.addFlashAttribute("exito", "Servicio guardado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/servicios";
    }

    // ELIMINAR (LÓGICO)
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes flash, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        Servicio serv = servicioDAO.findById(id).orElse(null);
        if (serv != null) {
            serv.setActivo(false);
            servicioDAO.save(serv);
            flash.addFlashAttribute("exito", "Servicio eliminado del catálogo.");
        }
        return "redirect:/admin/servicios";
    }

    // Helper de seguridad
    private boolean validarAcceso(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        return u != null && (u.getTipoUsuario().getNombreRol().equals("ADMIN") || u.getTipoUsuario().getNombreRol().equals("VENDEDOR"));
    }
}