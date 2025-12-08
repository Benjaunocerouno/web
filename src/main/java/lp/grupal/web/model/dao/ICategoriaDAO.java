package lp.grupal.web.model.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import lp.grupal.web.model.Categoria;
import java.util.List;

public interface ICategoriaDAO extends JpaRepository<Categoria, Integer> {
    List<Categoria> findByActivoTrue();
}