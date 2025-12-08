package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.Producto;
import lp.grupal.web.model.dao.IPedidoDAO;
import lp.grupal.web.model.dao.IProductoDAO;
import lp.grupal.web.model.dao.ISolicitudServicioDAO;
import lp.grupal.web.model.dao.IVentaDAO;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private IVentaDAO ventaDAO;
    @Autowired private IPedidoDAO pedidoDAO;
    @Autowired private ISolicitudServicioDAO solicitudServicioDAO;
    @Autowired private IProductoDAO productoDAO;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        
        // 1. SEGURIDAD
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        
        String rol = usuario.getTipoUsuario().getNombreRol();
        
        // CORRECCIÓN AQUÍ: Permitir entrar a ADMIN_VENDEDOR y CAJERO
        if (!rol.equals("ADMIN") && !rol.equals("VENDEDOR") && !rol.equals("ADMIN_VENDEDOR") && !rol.equals("CAJERO")) {
            return "redirect:/"; // Clientes fuera
        }

        // 2. DATOS TIENDA VIRTUAL
        long pedidosPendientesWeb = pedidoDAO.countByEstado("PENDIENTE");
        long solicitudesServicioWeb = solicitudServicioDAO.countByEstado("PENDIENTE");
        long totalPedidosWeb = pedidoDAO.count();

        // 3. DATOS TIENDA FÍSICA
        long totalVentasFisicas = ventaDAO.count();
        long serviciosRealizados = solicitudServicioDAO.countByEstado("FINALIZADO");

        // 4. ALERTAS DE STOCK
        List<Producto> productosBajos = productoDAO.encontrarStockBajo();

        // 5. ENVIAR A LA VISTA
        model.addAttribute("virtual_pendientes", pedidosPendientesWeb);
        model.addAttribute("virtual_servicios", solicitudesServicioWeb);
        model.addAttribute("virtual_total", totalPedidosWeb);

        model.addAttribute("fisica_ventas", totalVentasFisicas);
        model.addAttribute("fisica_servicios_ok", serviciosRealizados);
        model.addAttribute("alertasStock", productosBajos);

        return "empresa/dashboard"; 
    }
}