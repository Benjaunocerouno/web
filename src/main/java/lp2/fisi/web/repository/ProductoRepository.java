package lp2.fisi.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import lp2.fisi.web.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // ELIMINADO: List<Producto> findAll(); ← Ya viene de JpaRepository
}