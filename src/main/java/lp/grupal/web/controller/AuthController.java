package lp.grupal.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpSession;

// Imports correctos
import lp.grupal.web.model.dao.IUsuarioDAO;
import lp.grupal.web.model.dao.ITipoUsuarioDAO;
import lp.grupal.web.model.Usuario;      
import lp.grupal.web.model.TipoUsuario;

@Controller
public class AuthController {

    @Autowired
    private IUsuarioDAO usuarioDAO;

    @Autowired
    private ITipoUsuarioDAO tipoUsuarioDAO;

    // --- LOGIN ---
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        // Buscamos usuario
        Usuario usuario = usuarioDAO.findByEmailAndContrasena(email, password);

        if (usuario != null) {
            // CORRECCIÓN AQUÍ: Comparamos con 1 (Activo) en lugar de true
            // Si estado es 0, significa que está bloqueado
            if (usuario.getEstado() == 0) {
                redirectAttributes.addFlashAttribute("error", "Tu cuenta está inactiva/bloqueada.");
                return "redirect:/login";
            }
            
            // Login exitoso
            session.setAttribute("usuario", usuario); 
            
            // Redirección inteligente según ROL
            String rol = usuario.getTipoUsuario().getNombreRol();
            if (rol.equals("ADMIN") || rol.equals("VENDEDOR")) {
                return "redirect:/admin/dashboard"; // Empleados van al panel
            } else {
                return "redirect:/"; // Clientes van a la tienda
            }

        } else {
            redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas.");
            return "redirect:/login";
        }
    }

    // --- REGISTRO ---
    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam("nombres") String nombres,
                                   @RequestParam("username") String username,
                                   @RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   @RequestParam("telefono") String telefono,
                                   RedirectAttributes redirectAttributes) {

        if (usuarioDAO.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "El correo ya existe.");
            return "redirect:/registro";
        }

        try {
            Usuario nuevo = new Usuario();
            nuevo.setNombres(nombres);
            nuevo.setUsername(username);
            nuevo.setEmail(email);
            nuevo.setContrasena(password);
            nuevo.setTelefono(telefono);
            
            // Por defecto, estado 1 (Activo) lo pone el @PrePersist de la entidad, 
            // pero podemos asegurarlo aquí también.
            nuevo.setEstado(1); 
            
            // Asignar Rol 'CLIENTE'
            TipoUsuario rolCliente = tipoUsuarioDAO.findByNombreRol("CLIENTE");
            
            if (rolCliente == null) {
                 redirectAttributes.addFlashAttribute("error", "Error: No existe el rol CLIENTE en la base de datos.");
                 return "redirect:/registro";
            }
            
            nuevo.setTipoUsuario(rolCliente);
            usuarioDAO.save(nuevo);
            
            redirectAttributes.addFlashAttribute("exito", "¡Registro exitoso! Ahora inicia sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    // --- LOGOUT ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Borra la sesión
        return "redirect:/";
    }
}