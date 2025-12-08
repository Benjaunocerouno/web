package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.Producto;
import lp.grupal.web.model.Categoria;
import lp.grupal.web.model.dao.IProductoDAO;
import lp.grupal.web.model.dao.ICategoriaDAO;

import java.util.List;

@Controller
@RequestMapping("/admin/inventario")
public class InventarioController {

    @Autowired private IProductoDAO productoDAO;
    @Autowired private ICategoriaDAO categoriaDAO;

    // LISTAR PRODUCTOS
    @GetMapping
    public String listar(Model model, @RequestParam(value = "buscar", required = false) String buscar, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        List<Producto> productos;
        if (buscar != null && !buscar.isEmpty()) {
            productos = productoDAO.buscarPorCriterio(buscar);
        } else {
            productos = productoDAO.findByActivoTrue();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("palabraClave", buscar);
        return "empresa/inventario"; // HTML de la lista
    }

    // FORMULARIO NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaDAO.findByActivoTrue());
        model.addAttribute("titulo", "Nuevo Producto");
        return "empresa/form_producto"; // HTML del formulario
    }

    // FORMULARIO EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model, HttpSession session, RedirectAttributes flash) {
        if (!validarAcceso(session)) return "redirect:/login";

        Producto prod = productoDAO.findById(id).orElse(null);
        if (prod == null) {
            flash.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/admin/inventario";
        }

        model.addAttribute("producto", prod);
        model.addAttribute("categorias", categoriaDAO.findByActivoTrue());
        model.addAttribute("titulo", "Editar Producto");
        return "empresa/form_producto";
    }

    // GUARDAR (CREAR O ACTUALIZAR)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, RedirectAttributes flash, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        try {
            // Asegurar que si es nuevo tenga estado activo
            if(producto.getActivo() == null) producto.setActivo(true);
            
            productoDAO.save(producto);
            flash.addFlashAttribute("exito", "Producto guardado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/inventario";
    }

    // ELIMINAR (LÓGICO)
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes flash, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        Producto prod = productoDAO.findById(id).orElse(null);
        if (prod != null) {
            prod.setActivo(false); // No borramos de la BD, solo ocultamos
            productoDAO.save(prod);
            flash.addFlashAttribute("exito", "Producto eliminado del catálogo.");
        }
        return "redirect:/admin/inventario";
    }

    // Helper para seguridad
    private boolean validarAcceso(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        return u != null && (u.getTipoUsuario().getNombreRol().equals("ADMIN") || u.getTipoUsuario().getNombreRol().equals("VENDEDOR"));
    }
}