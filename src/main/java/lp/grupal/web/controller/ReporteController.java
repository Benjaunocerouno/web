package lp.grupal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lp.grupal.web.model.Usuario;
import lp.grupal.web.model.Venta;
import lp.grupal.web.model.Caja;
import lp.grupal.web.model.dao.IVentaDAO;
import lp.grupal.web.model.dao.ICajaDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired private IVentaDAO ventaDAO;
    @Autowired private ICajaDAO cajaDAO;

    @GetMapping
    public String verReportes(Model model, 
                              @RequestParam(value = "inicio", required = false) String fInicio,
                              @RequestParam(value = "fin", required = false) String fFin,
                              HttpSession session) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        
        String rol = usuario.getTipoUsuario().getNombreRol();
        Integer idEmpresa = usuario.getEmpresa().getIdempresa();

        // Fechas por defecto (Mes actual)
        LocalDateTime inicio = (fInicio == null || fInicio.isEmpty()) ? LocalDate.now().withDayOfMonth(1).atStartOfDay() : LocalDate.parse(fInicio).atStartOfDay();
        LocalDateTime fin = (fFin == null || fFin.isEmpty()) ? LocalDateTime.now() : LocalDate.parse(fFin).atTime(LocalTime.MAX);

        // --- 1. REPORTE VENTAS GENERALES (Para AdminVendedor y Vendedor) ---
        List<Venta> ventas = new ArrayList<>();
        if (rol.equals("ADMIN_VENDEDOR") || rol.equals("VENDEDOR") || rol.equals("ADMIN")) {
            ventas = ventaDAO.buscarPorEmpresaYFecha(idEmpresa, inicio, fin);
        }
        
        // --- 2. REPORTE VENTAS POR USUARIO (Solo AdminVendedor) ---
        List<Map<String, Object>> ventasPorUsuario = new ArrayList<>();
        if (rol.equals("ADMIN_VENDEDOR") || rol.equals("ADMIN")) {
            ventasPorUsuario = ventaDAO.reporteVentasPorUsuario(idEmpresa, inicio, fin);
        }

        // --- 3. REPORTE DE CAJA (Solo AdminVendedor y Cajero) ---
        List<Caja> movimientosCaja = new ArrayList<>();
        if (rol.equals("ADMIN_VENDEDOR") || rol.equals("CAJERO") || rol.equals("ADMIN")) {
            movimientosCaja = cajaDAO.buscarPorEmpresaYFecha(idEmpresa, inicio, fin);
        }

        // Cálculos Totales
        double totalVentas = ventas.stream().mapToDouble(Venta::getMonto_total).sum();
        double totalIngresosCaja = movimientosCaja.stream()
                .filter(c -> c.getTipoMovimiento().equals("INGRESO"))
                .mapToDouble(Caja::getMonto).sum();
        double totalEgresosCaja = movimientosCaja.stream()
                .filter(c -> c.getTipoMovimiento().equals("EGRESO"))
                .mapToDouble(Caja::getMonto).sum();

        // Enviar al HTML
        model.addAttribute("ventas", ventas);
        model.addAttribute("ventasPorUsuario", ventasPorUsuario);
        model.addAttribute("caja", movimientosCaja);
        
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("balanceCaja", totalIngresosCaja - totalEgresosCaja);
        
        model.addAttribute("fInicio", (fInicio != null) ? fInicio : inicio.toLocalDate().toString());
        model.addAttribute("fFin", (fFin != null) ? fFin : fin.toLocalDate().toString());
        model.addAttribute("rolUsuario", rol); // Para ocultar pestañas en HTML

        return "empresa/reportes";
    }
}