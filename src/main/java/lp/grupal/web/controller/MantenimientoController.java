package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.Empresa;
import lp.grupal.web.model.Proveedor;
import lp.grupal.web.model.dao.IEmpresaDAO;
import lp.grupal.web.model.dao.IUsuarioDAO;
import lp.grupal.web.model.dao.IProveedorDAO;
import lp.grupal.web.model.Compra; // Importante
import lp.grupal.web.model.dao.ICompraDAO; // Importante

import java.util.List;

@Controller
@RequestMapping("/admin/mantenimiento")
public class MantenimientoController {

    @Autowired private IEmpresaDAO empresaDAO;
    @Autowired private IUsuarioDAO usuarioDAO;
    @Autowired private IProveedorDAO proveedorDAO;
    @Autowired private ICompraDAO compraDAO; // Inyectar DAO de Compras

    @GetMapping
    public String index(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null || (!usuario.getTipoUsuario().getNombreRol().equals("ADMIN") && !usuario.getTipoUsuario().getNombreRol().equals("VENDEDOR"))) {
            return "redirect:/login";
        }

        // Cargar Empresa
        Empresa miEmpresa = usuario.getEmpresa();
        if (miEmpresa == null) {
            miEmpresa = empresaDAO.findById(1).orElse(new Empresa());
        }

        // Cargar Listas
        model.addAttribute("empresa", miEmpresa);
        model.addAttribute("usuario", usuario);
        model.addAttribute("proveedores", proveedorDAO.findByActivoTrue());
        
        // --- NUEVO: CARGAR COMPRAS ---
        model.addAttribute("compras", compraDAO.findAllByOrderByFechaCompraDesc());
        
        // Objetos para formularios
        model.addAttribute("nuevoProveedor", new Proveedor());
        model.addAttribute("nuevaCompra", new Compra()); // Objeto vacío para el formulario de compras

        return "empresa/mantenimiento";
    }

    // --- LOGICA EMPRESA ---
    @PostMapping("/empresa/guardar")
    public String guardarEmpresa(@ModelAttribute Empresa empresa, RedirectAttributes flash) {
        try {
            empresaDAO.save(empresa);
            flash.addFlashAttribute("exito", "Datos de empresa actualizados.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento";
    }
    
    // --- LOGICA PERFIL ---
    @PostMapping("/perfil/guardar")
    public String guardarPerfil(@ModelAttribute Usuario usuarioForm, HttpSession session, RedirectAttributes flash) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        try {
            Usuario usuarioBD = usuarioDAO.findById(usuarioSesion.getIdusuario()).get();
            usuarioBD.setNombres(usuarioForm.getNombres());
            usuarioBD.setTelefono(usuarioForm.getTelefono());
            if (usuarioForm.getContrasena() != null && !usuarioForm.getContrasena().isEmpty()) {
                usuarioBD.setContrasena(usuarioForm.getContrasena());
            }
            usuarioDAO.save(usuarioBD);
            session.setAttribute("usuario", usuarioBD);
            flash.addFlashAttribute("exito", "Perfil actualizado.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error perfil.");
        }
        return "redirect:/admin/mantenimiento";
    }

    // --- LOGICA PROVEEDORES ---
    
    @PostMapping("/proveedor/guardar")
    public String guardarProveedor(@ModelAttribute Proveedor proveedor, HttpSession session, RedirectAttributes flash) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        try {
            proveedor.setEmpresa(usuario.getEmpresa());
            if(proveedor.getActivo() == null) proveedor.setActivo(true);

            proveedorDAO.save(proveedor);
            flash.addFlashAttribute("exito", "Proveedor registrado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar proveedor: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento";
    }

    @GetMapping("/proveedor/eliminar/{id}")
    public String eliminarProveedor(@PathVariable("id") Integer id, RedirectAttributes flash) {
        Proveedor prov = proveedorDAO.findById(id).orElse(null);
        if (prov != null) {
            prov.setActivo(false); // Borrado lógico
            proveedorDAO.save(prov);
            flash.addFlashAttribute("exito", "Proveedor eliminado.");
        }
        return "redirect:/admin/mantenimiento";
    }

    // --- NUEVO: LOGICA COMPRAS ---

    @PostMapping("/compra/guardar")
    public String guardarCompra(@ModelAttribute Compra compra, HttpSession session, RedirectAttributes flash) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        // VALIDACIÓN DE ROL: Solo VENDEDOR (Rol 3)
        // Esto protege en el backend antes de llegar a la base de datos
        if (!usuario.getTipoUsuario().getNombreRol().equals("VENDEDOR")) {
            flash.addFlashAttribute("error", "Acceso denegado: Solo los VENDEDORES pueden registrar compras.");
            return "redirect:/admin/mantenimiento";
        }

        try {
            compra.setUsuario(usuario); // Asignamos el vendedor responsable
            compra.setEmpresa(usuario.getEmpresa()); // Asignamos la empresa
            
            compraDAO.save(compra);
            flash.addFlashAttribute("exito", "Compra registrada exitosamente.");
        } catch (Exception e) {
            // Aquí capturamos si el Trigger de la BD lanza error
            flash.addFlashAttribute("error", "Error al registrar compra: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento";
    }
}