package lp.grupal.web.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import lp.grupal.web.model.Servicio;
import java.util.List;

public interface IServicioDAO extends JpaRepository<Servicio, Integer> {
    
    List<Servicio> findByActivoTrue(); // Solo activos

    // Búsqueda por nombre o descripción
    @Query("SELECT s FROM Servicio s WHERE s.activo = true AND " +
           "(s.nombre LIKE %:criterio% OR s.descripcion LIKE %:criterio%)")
    List<Servicio> buscarPorCriterio(@Param("criterio") String criterio);
}