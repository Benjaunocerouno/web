package lp2.fisi.web.repository;

import lp2.fisi.web.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);

    @Query("SELECT c FROM Cliente c WHERE c.email = :email AND c.password = :password")
    Optional<Cliente> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}