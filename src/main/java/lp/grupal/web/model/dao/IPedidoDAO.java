package lp.grupal.web.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import lp.grupal.web.model.Pedido;
import java.util.List; // Importante agregar esto

public interface IPedidoDAO extends JpaRepository<Pedido, Integer> {

    long countByEstado(String estado);
    
    long count();

    // --- NUEVO MÉTODO AGREGADO ---
    // Busca pedidos por estado y los ordena del más reciente al más antiguo
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(String estado);
}