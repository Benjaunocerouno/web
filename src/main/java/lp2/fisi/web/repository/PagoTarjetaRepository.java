// PagoTarjetaRepository.java
package lp2.fisi.web.repository;

import lp2.fisi.web.model.PagoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoTarjetaRepository extends JpaRepository<PagoTarjeta, Integer> {
}