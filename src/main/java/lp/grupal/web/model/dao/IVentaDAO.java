package lp.grupal.web.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import lp.grupal.web.model.Venta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IVentaDAO extends JpaRepository<Venta, Integer> {
    
    // Ventas por fecha y empresa
    @Query("SELECT v FROM Venta v JOIN v.usuario u WHERE u.empresa.idempresa = :idEmpresa AND v.fechaventa BETWEEN :inicio AND :fin")
    List<Venta> buscarPorEmpresaYFecha(@Param("idEmpresa") Integer idEmpresa, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // REPORTE: Ventas agrupadas por Usuario (Vendedor)
    // Devuelve: nombre_vendedor, cantidad_ventas, total_vendido
    @Query(value = "SELECT u.nombres as vendedor, COUNT(v.idventa) as cantidad, SUM(v.monto_total) as total " +
                   "FROM venta v " +
                   "JOIN usuario u ON v.id_empleado = u.idusuario " +
                   "WHERE u.id_empresa = :idEmpresa AND v.fechaventa BETWEEN :inicio AND :fin " +
                   "GROUP BY u.idusuario", nativeQuery = true)
    List<Map<String, Object>> reporteVentasPorUsuario(@Param("idEmpresa") Integer idEmpresa, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);


    // NUEVO: Obtener el último número de comprobante registrado según el tipo
    @Query("SELECT MAX(v.num_comprobante) FROM Venta v WHERE v.tipo_comprobante = :tipo AND v.serie_comprobante = :serie")
    String obtenerUltimoNumero(@Param("tipo") String tipo, @Param("serie") String serie);
}