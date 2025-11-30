// PagoYapeRepository.java
package lp2.fisi.web.repository;

import lp2.fisi.web.model.PagoYape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoYapeRepository extends JpaRepository<PagoYape, Integer> {
}