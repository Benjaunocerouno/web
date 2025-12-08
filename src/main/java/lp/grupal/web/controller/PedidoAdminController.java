package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.Pedido;
import lp.grupal.web.model.dao.IPedidoDAO;

import java.util.List;

@Controller
@RequestMapping("/admin/pedidos-web")
public class PedidoAdminController {

    @Autowired
    private IPedidoDAO pedidoDAO;

    @GetMapping
    public String listarPedidosPendientes(Model model, HttpSession session) {
        
        // 1. SEGURIDAD: Verificar sesión y rol
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        String rol = usuario.getTipoUsuario().getNombreRol();
        if (!rol.equals("ADMIN") && !rol.equals("VENDEDOR") && !rol.equals("ADMIN_VENDEDOR")) {
            return "redirect:/"; // Si no es personal autorizado, fuera
        }

        // 2. OBTENER DATOS
        // Usamos el método que acabamos de crear en el DAO
        List<Pedido> pedidosPendientes = pedidoDAO.findByEstadoOrderByFechaPedidoDesc("PENDIENTE");

        // 3. ENVIAR A LA VISTA
        model.addAttribute("pedidos", pedidosPendientes);
        
        // Retornamos la plantilla HTML que crearemos a continuación
        return "empresa/pedidos_web"; 
    }
}