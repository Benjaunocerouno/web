package lp.grupal.web.model.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import lp.grupal.web.model.SolicitudServicio;

public interface ISolicitudServicioDAO extends JpaRepository<SolicitudServicio, Integer> {

    long countByEstado(String estado);
}