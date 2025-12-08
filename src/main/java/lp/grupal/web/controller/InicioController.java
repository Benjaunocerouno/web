package lp.grupal.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpSession; // Importante
import java.util.List;

import lp.grupal.web.model.Producto;
import lp.grupal.web.model.Servicio;
import lp.grupal.web.model.Usuario; // Importante
import lp.grupal.web.model.dao.IProductoDAO;
import lp.grupal.web.model.dao.IServicioDAO;

@Controller
public class InicioController {

    @Autowired private IProductoDAO productoDAO;
    @Autowired private IServicioDAO servicioDAO;

    @GetMapping({"/", "/index", "/home"}) 
    public String inicio(Model model, HttpSession session) {
        
        // 1. VERIFICAR SI ES EMPLEADO (ADMIN O VENDEDOR)
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario != null) {
            String rol = usuario.getTipoUsuario().getNombreRol();
            // Si es personal de la empresa, NO mostrar la tienda, mandar al Dashboard
            if (rol.equals("ADMIN") || rol.equals("VENDEDOR")) {
                return "redirect:/admin/dashboard";
            }
        }

        // 2. SI ES CLIENTE O VISITANTE, MOSTRAR LA TIENDA
        // Usamos try-catch por seguridad si falla la consulta
        try {
            List<Producto> productos = productoDAO.findTop8ByActivoTrueOrderByIdproductoDesc();
            List<Servicio> servicios = servicioDAO.findByActivoTrue(); // Cambiado a findByActivoTrue() que es el estándar

            model.addAttribute("listaProductos", productos);
            model.addAttribute("listaServicios", servicios);
        } catch (Exception e) {
            System.out.println("Error cargando productos: " + e.getMessage());
        }

        return "index";
    }
}