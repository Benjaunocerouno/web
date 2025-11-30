// PagoEfectivoRepository.java
package lp2.fisi.web.repository;

import lp2.fisi.web.model.PagoEfectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoEfectivoRepository extends JpaRepository<PagoEfectivo, Integer> {
}