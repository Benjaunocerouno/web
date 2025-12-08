package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.Venta;
import lp.grupal.web.model.DetallePedido;
import lp.grupal.web.model.DetalleVenta;
import lp.grupal.web.model.Pedido;
import lp.grupal.web.model.dao.IDetalleVentaDAO;
import lp.grupal.web.model.dao.IPedidoDAO;
import lp.grupal.web.model.dao.IVentaDAO;

import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 


import java.util.List;

@Controller
@RequestMapping("/admin/pedidos-web")
public class PedidoAdminController {

    @Autowired private IVentaDAO ventaDAO;
    @Autowired private IDetalleVentaDAO detalleVentaDAO;

    @Autowired
    private IPedidoDAO pedidoDAO;

    @GetMapping("/atender/{id}")
    public String atenderPedido(@PathVariable("id") Integer idPedido, HttpSession session, RedirectAttributes flash) {
        
        // 1. Validar Seguridad (Admin/Vendedor)
        Usuario usuarioAdmin = (Usuario) session.getAttribute("usuario");
        if (usuarioAdmin == null) return "redirect:/login";

        // 2. Buscar el Pedido
        Pedido pedido = pedidoDAO.findById(idPedido).orElse(null);
        if (pedido == null) {
            flash.addFlashAttribute("error", "El pedido no existe.");
            return "redirect:/admin/pedidos-web";
        }

        if (!pedido.getEstado().equals("PENDIENTE")) {
            flash.addFlashAttribute("error", "El pedido ya fue procesado anteriormente.");
            return "redirect:/admin/pedidos-web";
        }

        try {
            // 3. Crear la VENTA (Cabecera) basándonos en el Pedido
            Venta nuevaVenta = new Venta();
            nuevaVenta.setUsuario(pedido.getUsuario()); 
            nuevaVenta.setEstado("COMPLETADO");
            nuevaVenta.setMonto_total(pedido.getMontoEstimado());
            nuevaVenta.setDireccion_envio(pedido.getObservacionesCliente()); 
            nuevaVenta.setTipo_comprobante("BOLETA"); 
            
            // Guardamos la venta para generar el ID
            Venta ventaGuardada = ventaDAO.save(nuevaVenta);

            // 4. Copiar Detalles (De DetallePedido a DetalleVenta)
            for (DetallePedido dp : pedido.getDetalles()) {
                DetalleVenta dv = new DetalleVenta();
                dv.setVenta(ventaGuardada);
                dv.setProducto(dp.getProducto());
                dv.setCantidad(dp.getCantidad());
                dv.setPrecioUnitario(dp.getPrecioReferencial());
                
                // Guardamos el detalle
                detalleVentaDAO.save(dv);
            }

            // 5. Actualizar el PEDIDO Original
            pedido.setEstado("APROBADO");
            pedido.setIdVentaGenerada(ventaGuardada.getIdventa()); // ¡Aquí vinculamos!
            pedido.setIdVendedorRevisor(usuarioAdmin.getIdusuario()); // Guardamos quién lo aprobó
            pedidoDAO.save(pedido);

            flash.addFlashAttribute("exito", "Pedido #" + idPedido + " aprobado. Se generó la Venta #" + ventaGuardada.getIdventa());

        } catch (Exception e) {
            e.printStackTrace();
            flash.addFlashAttribute("error", "Error al procesar: " + e.getMessage());
        }

        return "redirect:/admin/pedidos-web";
    }



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