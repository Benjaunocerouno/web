package lp2.fisi.web.repository;

import lp2.fisi.web.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findByActivoTrue();
    List<Servicio> findByCategoriaAndActivoTrue(String categoria);
}