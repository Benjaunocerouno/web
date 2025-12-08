package lp.grupal.web.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Importante
import lp.grupal.web.model.Usuario;
import java.util.List;
import java.util.Map;

public interface IUsuarioDAO extends JpaRepository<Usuario, Integer> {

    // Login
    Usuario findByEmailAndContrasena(String email, String contrasena);
    Usuario findByEmail(String email);

    // Búsqueda de clientes
    @Query("SELECT u FROM Usuario u WHERE u.tipoUsuario.idtipousuario = 2 AND u.nombres LIKE %:keyword%")
    List<Usuario> buscarClientes(@Param("keyword") String keyword);

    // Listar todos los clientes
    @Query("SELECT u FROM Usuario u WHERE u.tipoUsuario.idtipousuario = 2")
    List<Usuario> listarClientes();

    // Cliente Top
    @Query(value = "CALL sp_cliente_top_compras()", nativeQuery = true)
    Map<String, Object> obtenerClienteTop();
}
