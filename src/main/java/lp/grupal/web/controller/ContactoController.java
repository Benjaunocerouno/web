package lp.grupal.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lp.grupal.web.model.dao.IEmpresaDAO;
import lp.grupal.web.model.Empresa;

@Controller
public class ContactoController {
    @Autowired
    private IEmpresaDAO empresaDAO;
    
    @GetMapping("/contacto")
    public String contacto(Model model) {
        
        Empresa miEmpresa = empresaDAO.findById(1).orElse(new Empresa());
        
        model.addAttribute("empresa", miEmpresa);

        return "contacto";
    }
    
    @PostMapping("/contacto/enviar")
    public String enviarMensaje(
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("asunto") String asunto,
            @RequestParam("mensaje") String mensaje,
            RedirectAttributes redirectAttributes) {
                
        System.out.println("=== NUEVO MENSAJE DE CONTACTO ===");
        System.out.println("De: " + nombre + " (" + email + ")");
        System.out.println("Asunto: " + asunto);
        System.out.println("Mensaje: " + mensaje);
        System.out.println("=================================");
        
        redirectAttributes.addFlashAttribute("mensajeExito", "¡Gracias por escribirnos! Hemos recibido tu mensaje y te responderemos pronto.");

        return "redirect:/contacto";
    }
}