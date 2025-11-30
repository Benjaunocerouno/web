package lp2.fisi.web.controller;

import lp2.fisi.web.model.Cliente;
import lp2.fisi.web.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private ClienteService clienteService;

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    // Procesar registro
    @PostMapping("/registro/procesar")
    public String procesarRegistro(
            @RequestParam String dni,
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String telefono,
            @RequestParam String email,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fecha_nacimiento,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        try {
            LocalDate fechaNacimiento = null;
            if (fecha_nacimiento != null && !fecha_nacimiento.isEmpty()) {
                fechaNacimiento = LocalDate.parse(fecha_nacimiento);
            }

            Cliente cliente = clienteService.registrarCliente(
                    dni, nombres, apellidos, telefono, email,
                    direccion, fechaNacimiento, password
            );

            redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso");
            return "redirect:/auth/login?success=true";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/registro?error=true";
        }
    }

    // Mostrar login
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) Boolean success,
                               @RequestParam(required = false) Boolean error,
                               Model model) {
        if (Boolean.TRUE.equals(success)) {
            model.addAttribute("mensaje", "Registro exitoso. Ya puedes iniciar sesión.");
        }
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", "Error en el registro. Intenta nuevamente.");
        }
        return "login";
    }

    // Procesar login
    @PostMapping("/login/procesar")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session, // <--- 1. Agregamos el objeto HttpSession
            RedirectAttributes redirectAttributes) {

        try {
            var clienteOpt = clienteService.login(email, password);

            if (clienteOpt.isPresent()) {
                // 2. Guardamos el cliente en la sesión PERMANENTE (hasta que cierre sesión)
                session.setAttribute("usuario", clienteOpt.get()); 
                
                return "redirect:/"; // Redirigir al home
            } else {
                redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas");
                return "redirect:/auth/login?error=true";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error en el login");
            return "redirect:/auth/login?error=true";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) { // <--- Recibir sesión
        session.invalidate(); // <--- 3. Destruir la sesión por completo
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada correctamente");
        return "redirect:/auth/login";
    }
}