package lp.grupal.web.model.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import lp.grupal.web.model.Pedido;

public interface IPedidoDAO extends JpaRepository<Pedido, Integer> {

    long countByEstado(String estado);
    
    long count();
}