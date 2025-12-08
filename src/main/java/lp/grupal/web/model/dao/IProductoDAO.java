package lp.grupal.web.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Importante para @Param
import lp.grupal.web.model.Producto;
import java.util.List;

public interface IProductoDAO extends JpaRepository<Producto, Integer> {
    
    // Método para la tienda (listar activos)
    List<Producto> findByActivoTrue();

    // ESTE ES EL QUE FALTABA PARA INICIOCONTROLLER:
    List<Producto> findTop8ByActivoTrueOrderByIdproductoDesc();

    // Búsqueda del admin
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(p.nombre LIKE %:criterio% OR p.marca LIKE %:criterio% OR p.codigoBarras LIKE %:criterio%)")
    List<Producto> buscarPorCriterio(@Param("criterio") String criterio);

    // Alerta de stock
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> encontrarStockBajo();
}