package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.dao.IUsuarioDAO;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioDAO usuarioDAO;

    @GetMapping
    public String listarUsuarios(Model model, @RequestParam(value = "buscar", required = false) String buscar, HttpSession session) {
        
        // 1. SEGURIDAD
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null) return "redirect:/login";

        String rol = usuarioSesion.getTipoUsuario().getNombreRol();
        if (!rol.equals("ADMIN") && !rol.equals("VENDEDOR")) {
            return "redirect:/"; 
        }

        // 2. BUSQUEDA
        List<Usuario> listaClientes;
        if (buscar != null && !buscar.isEmpty()) {
            listaClientes = usuarioDAO.buscarClientes(buscar);
        } else {
            listaClientes = usuarioDAO.listarClientes();
        }
        
        // 3. CLIENTE TOP (CON PROTECCIÓN DE ERROR)
        Map<String, Object> topCliente = null;
        try {
            // Esto puede fallar si el procedimiento no devuelve nada
            topCliente = usuarioDAO.obtenerClienteTop();
        } catch (Exception e) {
            // Si falla, simplemente dejamos topCliente como null
            // Así la página carga igual, pero sin la tarjeta del ganador
            System.out.println("Aun no hay cliente top o fallo el SP");
        }
        
        model.addAttribute("clientes", listaClientes);
        model.addAttribute("topCliente", topCliente);
        model.addAttribute("palabraClave", buscar);

        return "empresa/usuarios"; 
    }

    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable("id") Integer id, RedirectAttributes flash) {
        Usuario user = usuarioDAO.findById(id).orElse(null);
        
        if (user != null) {
            // Lógica corregida para Integer (1 o 0)
            int estadoActual = (user.getEstado() == null) ? 1 : user.getEstado();
            int nuevoEstado = (estadoActual == 1) ? 0 : 1;
            
            user.setEstado(nuevoEstado);
            usuarioDAO.save(user);
            
            String msj = (nuevoEstado == 1) ? "Usuario desbloqueado." : "Usuario bloqueado.";
            flash.addFlashAttribute("exito", msj);
        } else {
            flash.addFlashAttribute("error", "Usuario no encontrado.");
        }
        
        return "redirect:/admin/usuarios";
    }
}