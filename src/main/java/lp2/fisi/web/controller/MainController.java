package lp2.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lp2.fisi.web.model.Cliente;
import lp2.fisi.web.service.ProductoService;
import lp2.fisi.web.service.ServicioService;

@Controller
public class MainController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/servicios")
    public String servicios(Model model) {
        model.addAttribute("servicios", servicioService.obtenerServiciosActivos());
        return "servicios";
    }

    @PostMapping("/servicios/solicitar")
    public String solicitarServicio(
            @RequestParam Integer servicioId,
            @RequestParam String nombre,
            @RequestParam String dni,
            @RequestParam String telefono,
            @RequestParam String email,
            @RequestParam String dispositivo,
            @RequestParam String descripcion,
            @RequestParam String fechaCita,
            HttpSession session) { // <--- 1. Pedimos la sesión

        // 2. Intentamos obtener el cliente de la sesión
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        Integer idCliente = (cliente != null) ? cliente.getIdCliente() : null;

        // 3. Pasamos el ID al servicio
        servicioService.procesarSolicitud(servicioId, nombre, dni, telefono, email, 
                                          dispositivo, descripcion, fechaCita, idCliente);
                                          
        return "redirect:/servicios?exito=true";
    }

    @GetMapping("/tienda")
    public String tienda(Model model) {
        model.addAttribute("productos", productoService.obtenerTodosProductos());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        return "tienda";
    }

    @GetMapping("/nosotros") public String nosotros() { return "nosotros";}

    @GetMapping("/contacto") public String contacto() { return "contacto";}

    @GetMapping("/login") public String login() { return "login";}

    @GetMapping("/registro") public String registro() { return "registro";}
}