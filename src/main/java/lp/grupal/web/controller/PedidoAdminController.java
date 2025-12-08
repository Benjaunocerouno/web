package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.*;
import lp.grupal.web.model.dao.*;

import java.util.List;

@Controller
@RequestMapping("/admin/pedidos-web")
public class PedidoAdminController {

    @Autowired private IPedidoDAO pedidoDAO;
    @Autowired private IVentaDAO ventaDAO;
    @Autowired private IDetalleVentaDAO detalleVentaDAO;
    @Autowired private ICajaDAO cajaDAO; 

    // 1. LISTAR PEDIDOS
    @GetMapping
    public String listarPedidosPendientes(Model model, HttpSession session) {
        if (!validarAcceso(session)) return "redirect:/login";

        List<Pedido> pedidosPendientes = pedidoDAO.findByEstadoOrderByFechaPedidoDesc("PENDIENTE");
        model.addAttribute("pedidos", pedidosPendientes);
        return "empresa/pedidos_web"; 
    }

    // 2. REVISAR Y PRE-LLENAR DATOS
    @GetMapping("/revisar/{id}")
    public String revisarPedido(@PathVariable("id") Integer idPedido, Model model, HttpSession session, RedirectAttributes flash) {
        if (!validarAcceso(session)) return "redirect:/login";

        Pedido pedido = pedidoDAO.findById(idPedido).orElse(null);
        if (pedido == null || !pedido.getEstado().equals("PENDIENTE")) {
            flash.addFlashAttribute("error", "El pedido no existe o ya fue atendido.");
            return "redirect:/admin/pedidos-web";
        }

        model.addAttribute("pedido", pedido);
        
        // --- LOGICA PARA PRE-LLENAR DATOS ---
        // Por defecto sugerimos BOLETA y calculamos el siguiente número
        String siguienteNumero = generarSiguienteNumero("BOLETA", "B001");
        
        model.addAttribute("serieSugerida", "B001");
        model.addAttribute("numeroSugerido", siguienteNumero);
        
        return "empresa/atender_pedido"; 
    }

    // 3. PROCESAR VENTA (CON TRANSACCIÓN)
    @PostMapping("/procesar")
    @Transactional
    public String procesarVenta(@RequestParam("idPedido") Integer idPedido,
                                @RequestParam("tipoComprobante") String tipoComprobante,
                                @RequestParam("serieComprobante") String serieComprobante,
                                @RequestParam("numComprobante") String numComprobante, 
                                HttpSession session, 
                                RedirectAttributes flash) {
        
        Usuario usuarioAdmin = (Usuario) session.getAttribute("usuario");
        if (usuarioAdmin == null) return "redirect:/login";
        
        try {
            Pedido pedido = pedidoDAO.findById(idPedido).orElse(null);
            
            // Validación de seguridad por si otro admin ya lo atendió
            if (pedido == null || !pedido.getEstado().equals("PENDIENTE")) { 
                 flash.addFlashAttribute("error", "Error: El pedido ya no está pendiente.");
                 return "redirect:/admin/pedidos-web";
            }

            // A. Crear la VENTA (Cabecera)
            Venta nuevaVenta = new Venta();
            nuevaVenta.setUsuario(pedido.getUsuario());
            nuevaVenta.setEmpleado(usuarioAdmin);
            nuevaVenta.setEstado("COMPLETADO");
            nuevaVenta.setMonto_total(pedido.getMontoEstimado());
            nuevaVenta.setDireccion_envio(pedido.getObservacionesCliente());
            
            // Datos del comprobante recibidos del formulario
            nuevaVenta.setTipo_comprobante(tipoComprobante);
            nuevaVenta.setSerie_comprobante(serieComprobante);
            nuevaVenta.setNum_comprobante(numComprobante); 
            
            // Guardamos la venta primero para obtener su ID
            Venta ventaGuardada = ventaDAO.save(nuevaVenta);
            
            // B. Copiar Detalles (De Pedido a Venta)
            for (DetallePedido dp : pedido.getDetalles()) {
                 DetalleVenta dv = new DetalleVenta();
                 dv.setVenta(ventaGuardada);
                 dv.setProducto(dp.getProducto());
                 dv.setCantidad(dp.getCantidad());
                 dv.setPrecioUnitario(dp.getPrecioReferencial());
                 
                 // Esto disparará el Trigger en BD para descontar stock (asegúrate de haber borrado la resta manual en VentaController)
                 detalleVentaDAO.save(dv);
            }
            
            // C. Actualizar el Estado del PEDIDO
            pedido.setEstado("APROBADO");
            pedido.setIdVentaGenerada(ventaGuardada.getIdventa());
            pedido.setIdVendedorRevisor(usuarioAdmin.getIdusuario());
            pedidoDAO.save(pedido);

            // D. Registrar el Ingreso en CAJA
            Caja movimiento = new Caja();
            movimiento.setTipoMovimiento("INGRESO");
            movimiento.setConcepto("Venta Web #" + ventaGuardada.getIdventa() + " - " + tipoComprobante + " " + serieComprobante + "-" + numComprobante);
            movimiento.setMonto(pedido.getMontoEstimado());
            // Asumimos transferencia porque es web, o puedes agregar un campo en el formulario para esto
            movimiento.setMetodoPago("WEB/TRANSFERENCIA"); 
            movimiento.setVenta(ventaGuardada);
            movimiento.setPedido(pedido);
            movimiento.setEmpresa(usuarioAdmin.getEmpresa());
            movimiento.setUsuarioResponsable(usuarioAdmin);
            
            cajaDAO.save(movimiento);

            flash.addFlashAttribute("exito", "Venta procesada correctamente. Comprobante: " + serieComprobante + "-" + numComprobante);

        } catch (Exception e) {
            e.printStackTrace();
            flash.addFlashAttribute("error", "Error al procesar: " + e.getMessage());
            // Al ser @Transactional, si falla aquí, se revierte todo (venta, detalles y caja).
        }

        return "redirect:/admin/pedidos-web";
    }

    // Helper: Validar Rol
    private boolean validarAcceso(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        return u != null && (u.getTipoUsuario().getNombreRol().equals("ADMIN") || 
                             u.getTipoUsuario().getNombreRol().equals("VENDEDOR") || 
                             u.getTipoUsuario().getNombreRol().equals("ADMIN_VENDEDOR"));
    }

    // Helper: Generar Correlativo
    private String generarSiguienteNumero(String tipo, String serie) {
        // Busca el último número en la BD usando el DAO
        String ultimo = ventaDAO.obtenerUltimoNumero(tipo, serie);
        
        int numeroSiguiente = 1; // Por defecto empezamos en 1
        
        if (ultimo != null) {
            try {
                // Intenta convertir el string (ej: "000045") a entero y suma 1
                numeroSiguiente = Integer.parseInt(ultimo) + 1;
            } catch (NumberFormatException e) {
                numeroSiguiente = 1;
            }
        }
        
        // Formatea a 6 dígitos con ceros a la izquierda (Ej: 1 -> "000001")
        return String.format("%06d", numeroSiguiente);
    }
}