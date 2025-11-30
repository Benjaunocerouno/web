package lp2.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @RequestParam String fechaCita) {

        servicioService.procesarSolicitud(servicioId, nombre, dni, telefono, email, dispositivo, descripcion, fechaCita);
        return "redirect:/servicios?exito=true";
    }

    @GetMapping("/tienda")
    public String tienda(Model model) {
        model.addAttribute("productos", productoService.obtenerTodosProductos());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        return "tienda";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }
}