package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lp.grupal.web.model.Venta;
import lp.grupal.web.model.DetalleVenta;
import lp.grupal.web.model.Producto;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.Pedido;
import lp.grupal.web.model.DetallePedido;
import lp.grupal.web.model.Caja; // IMPORTANTE

import lp.grupal.web.model.dao.IVentaDAO;
import lp.grupal.web.model.dao.IProductoDAO;
import lp.grupal.web.model.dao.IPedidoDAO;
import lp.grupal.web.model.dao.IDetallePedidoDAO;
import lp.grupal.web.model.dao.ICajaDAO; // IMPORTANTE

@Controller
public class VentaController {

    @Autowired private IPedidoDAO pedidoDAO;
    @Autowired private IVentaDAO ventaDAO;
    @Autowired private IProductoDAO productoDAO;
    @Autowired private ICajaDAO cajaDAO; // Inyección de Caja

    // --- CLASES DTO AUXILIARES ---
    public static class ItemCarritoDTO {
        public Integer idproducto;
        public Integer cantidad;
        public Double precio; 
    }

    public static class SolicitudCompraDTO {
        public List<ItemCarritoDTO> carrito;
        public String direccion;
        public String ciudad;
        public String referencia;
        public String telefono;
        public String tipoPago;
    }

    // --- ENDPOINT 1: PROCESAMIENTO COMPLETO (Desde /checkout) ---
    @PostMapping("/pedidos/guardar")
    @ResponseBody
    public ResponseEntity<?> procesarPedido(@RequestBody SolicitudCompraDTO solicitud, HttpSession session) {
        
        Map<String, Object> respuesta = new HashMap<>();
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Sesión expirada."));
        }

        // Crear Pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUsuario(usuarioLogueado);
        nuevoPedido.setEstado("PENDIENTE"); // O 'PAGADO' si asumes que ya pagó
        
        String obs = "Dirección: " + solicitud.direccion + " | Ciudad: " + solicitud.ciudad 
                   + " | Ref: " + solicitud.referencia + " | Tel: " + solicitud.telefono 
                   + " | Pago: " + solicitud.tipoPago;
        nuevoPedido.setObservacionesCliente(obs);

        List<DetallePedido> detalles = new ArrayList<>();
        double totalCalculado = 0.0;

        if (solicitud.carrito != null) {
            for (ItemCarritoDTO item : solicitud.carrito) {
                Producto prodBD = productoDAO.findById(item.idproducto).orElse(null);
                if (prodBD != null) {
                    DetallePedido det = new DetallePedido();
                    det.setPedido(nuevoPedido);
                    det.setProducto(prodBD);
                    det.setCantidad(item.cantidad);
                    det.setPrecioReferencial(prodBD.getPrecio());
                    detalles.add(det);
                    totalCalculado += (prodBD.getPrecio() * item.cantidad);
                    
                    /*
                    prodBD.setStock(prodBD.getStock() - item.cantidad);
                    productoDAO.save(prodBD);
                    */
                    
                }
            }
        }
        
        nuevoPedido.setMontoEstimado(totalCalculado);
        nuevoPedido.setDetalles(detalles);

        try {
            // 1. Guardar Pedido
            Pedido pedidoGuardado = pedidoDAO.save(nuevoPedido);
            
            // 2. REGISTRAR EN CAJA
            Caja movimiento = new Caja();
            movimiento.setTipoMovimiento("INGRESO");
            movimiento.setConcepto("Pago Web - Pedido #" + pedidoGuardado.getIdpedido());
            movimiento.setMonto(totalCalculado);
            movimiento.setMetodoPago(solicitud.tipoPago);
            movimiento.setPedido(pedidoGuardado); // Enlazamos
            // Usuario responsable es null (sistema) o el cliente si quisieras
            
            cajaDAO.save(movimiento);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Pedido #" + nuevoPedido.getIdpedido() + " registrado."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // --- ENDPOINT 2: VENTA RÁPIDA (Desde el botón "Finalizar Compra" del carrito lateral) ---
    @PostMapping("/ventas/checkout")
    @ResponseBody 
    public ResponseEntity<?> procesarVentaRapida(@RequestBody List<ItemCarritoDTO> carrito, HttpSession session) {
        
        Map<String, Object> respuesta = new HashMap<>();
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Debes iniciar sesión."));
        }

        if (carrito == null || carrito.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "El carrito está vacío."));
        }

        // Crear Venta Simple
        Venta nuevaVenta = new Venta();
        nuevaVenta.setUsuario(usuarioLogueado);
        nuevaVenta.setEstado("PAGADO"); // Venta rápida asume pago inmediato
        nuevaVenta.setDireccion_envio("Retiro en Tienda (Venta Rápida)"); 
        
        List<DetalleVenta> detalles = new ArrayList<>();
        double totalCalculado = 0.0;

        for (ItemCarritoDTO item : carrito) {
            Producto prodBD = productoDAO.findById(item.idproducto).orElse(null);
            
            if (prodBD != null && prodBD.getActivo()) {
                if (prodBD.getStock() < item.cantidad) {
                    return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Sin stock: " + prodBD.getNombre()));
                }

                DetalleVenta det = new DetalleVenta();
                det.setVenta(nuevaVenta);
                det.setProducto(prodBD);
                det.setCantidad(item.cantidad);
                det.setPrecioUnitario(prodBD.getPrecio());

                detalles.add(det);
                totalCalculado += (prodBD.getPrecio() * item.cantidad);
                
                // Restar stock
                prodBD.setStock(prodBD.getStock() - item.cantidad);
                productoDAO.save(prodBD);
            }
        }

        nuevaVenta.setMonto_total(totalCalculado);
        nuevaVenta.setDetalles(detalles);

        try {
            // 1. Guardar Venta
            Venta ventaGuardada = ventaDAO.save(nuevaVenta);
            
            // 2. REGISTRAR EN CAJA
            Caja movimiento = new Caja();
            movimiento.setTipoMovimiento("INGRESO");
            movimiento.setConcepto("Venta Rápida Web #" + ventaGuardada.getIdventa());
            movimiento.setMonto(totalCalculado);
            movimiento.setMetodoPago("PENDIENTE"); // O 'EFECTIVO' si es en tienda
            movimiento.setVenta(ventaGuardada); // Enlazamos
            
            cajaDAO.save(movimiento);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Compra rápida realizada! Ticket #" + nuevaVenta.getIdventa()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}