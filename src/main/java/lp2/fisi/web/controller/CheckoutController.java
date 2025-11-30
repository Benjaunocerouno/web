package lp2.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.type.TypeReference; // IMPORTANTE
import com.fasterxml.jackson.databind.ObjectMapper;   // IMPORTANTE
import java.util.List;
import java.util.Map;

// Importamos todos los modelos y repositorios necesarios
import lp2.fisi.web.model.*;
import lp2.fisi.web.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class CheckoutController {

    // 1. Inyectamos el repositorio principal de PEDIDOS
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository; // <--- Inyectar nuevo repo

    // 2. Inyectamos los repositorios de pagos
    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private PagoYapeRepository pagoYapeRepository;

    @Autowired
    private PagoEfectivoRepository pagoEfectivoRepository;

    @Autowired
    private PagoTarjetaRepository pagoTarjetaRepository;



    @GetMapping("/checkout")
    public String mostrarCheckout(Model model) {
        model.addAttribute("titulo", "Finalizar Compra - TecnoAccesorios");
        return "checkout";
    }

    @PostMapping("/checkout/procesar")
    public String procesarPedido(
            // Datos del Cliente / Pedido
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String metodoPago,
            @RequestParam double total,
            @RequestParam String cartData,

            // Parámetros opcionales para Transferencia
            @RequestParam(required = false) String bancoOrigen,
            @RequestParam(required = false) String bancoDestino,
            @RequestParam(required = false) String numeroOperacion,
            @RequestParam(required = false) String nombreRemitente,
            @RequestParam(required = false) String dniRemitente,

            // Parámetros opcionales para Yape
            @RequestParam(required = false) String numeroCelular,
            @RequestParam(required = false) String nombreRemitenteYape,
            @RequestParam(required = false) String dniRemitenteYape,

            // Parámetros opcionales para Tarjeta
            @RequestParam(required = false) String tipoTarjeta,
            @RequestParam(required = false) String ultimosDigitos,

            Model model) {

        try {
            // PASO 1: Crear y Guardar el Pedido Principal
            // Esto generará un ID único en la base de datos
            Pedido pedido = new Pedido(nombre, email, telefono, direccion, BigDecimal.valueOf(total));
            Pedido pedidoGuardado = pedidoRepository.save(pedido);
            
            // Obtenemos el ID real generado
            Integer idPedidoReal = pedidoGuardado.getIdPedido();

            // 2. PROCESAR EL JSON DE PRODUCTOS Y GUARDAR DETALLES
            ObjectMapper mapper = new ObjectMapper();
            // Convertimos el String JSON a una Lista de Mapas
            List<Map<String, Object>> productos = mapper.readValue(cartData, new TypeReference<List<Map<String, Object>>>(){});

            for (Map<String, Object> prod : productos) {
                // Extraemos datos del JSON (LocalStorage)
                Integer idProducto = Integer.valueOf(prod.get("id").toString());
                Integer cantidad = Integer.valueOf(prod.get("quantity").toString());
                BigDecimal precio = BigDecimal.valueOf(Double.valueOf(prod.get("price").toString()));

                // Creamos el detalle y lo guardamos
                DetallePedido detalle = new DetallePedido(idPedidoReal, idProducto, cantidad, precio);
                detallePedidoRepository.save(detalle);
            }
            
            System.out.println("✅ Pedido guardado con ID: " + idPedidoReal);

            // PASO 2: Guardar el Pago vinculado al ID del Pedido
            switch(metodoPago) {
                case "transferencia":
                    Transferencia transferencia = new Transferencia();
                    transferencia.setIdPedido(idPedidoReal); // Vinculamos con el ID real
                    transferencia.setMonto(BigDecimal.valueOf(total));
                    transferencia.setBancoOrigen(bancoOrigen != null ? bancoOrigen : "No especificado");
                    transferencia.setBancoDestino(bancoDestino != null ? bancoDestino : "BCP");
                    transferencia.setNumeroOperacion(numeroOperacion != null ? numeroOperacion : "PENDIENTE");
                    transferencia.setFechaTransferencia(LocalDate.now());
                    transferencia.setHoraTransferencia(LocalTime.now());
                    transferencia.setNombreRemitente(nombreRemitente != null ? nombreRemitente : nombre);
                    transferencia.setDniRemitente(dniRemitente != null ? dniRemitente : "00000000");
                    transferencia.setEstado("pendiente");
                    
                    transferenciaRepository.save(transferencia);
                    break;

                case "yape":
                    PagoYape pagoYape = new PagoYape();
                    pagoYape.setIdPedido(idPedidoReal); // Vinculamos con el ID real
                    pagoYape.setMonto(BigDecimal.valueOf(total));
                    pagoYape.setNumeroCelular(numeroCelular != null ? numeroCelular : telefono);
                    pagoYape.setNombreRemitente(nombreRemitenteYape != null ? nombreRemitenteYape : nombre);
                    pagoYape.setDniRemitente(dniRemitenteYape != null ? dniRemitenteYape : "00000000");
                    pagoYape.setEstado("pendiente");
                    
                    pagoYapeRepository.save(pagoYape);
                    break;

                case "efectivo":
                    PagoEfectivo pagoEfectivo = new PagoEfectivo();
                    pagoEfectivo.setIdPedido(idPedidoReal); // Vinculamos con el ID real
                    pagoEfectivo.setMonto(BigDecimal.valueOf(total));
                    pagoEfectivo.setDireccionEntrega(direccion);
                    pagoEfectivo.setEstado("pendiente");
                    
                    pagoEfectivoRepository.save(pagoEfectivo);
                    break;

                case "tarjeta":
                    PagoTarjeta pagoTarjeta = new PagoTarjeta();
                    pagoTarjeta.setIdPedido(idPedidoReal); // Vinculamos con el ID real
                    pagoTarjeta.setMonto(BigDecimal.valueOf(total));
                    pagoTarjeta.setTipoTarjeta(tipoTarjeta != null ? tipoTarjeta : "crédito");
                    pagoTarjeta.setUltimosDigitos(ultimosDigitos != null ? ultimosDigitos : "0000");
                    pagoTarjeta.setEstado("pendiente");
                    
                    pagoTarjetaRepository.save(pagoTarjeta);
                    break;
            }

            // Pasamos datos a la vista de confirmación
            model.addAttribute("nombreCliente", nombre);
            model.addAttribute("metodoPago", metodoPago);
            model.addAttribute("idPedido", idPedidoReal); // Útil mostrar el número de pedido al cliente
            model.addAttribute("fechaEntrega", "2-3 días hábiles");

            return "confirmacion";

        } catch (Exception e) {
            System.err.println("❌ Error al procesar el pedido: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/checkout?error=true";
        }
    }
}