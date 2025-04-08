package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Sale;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
}
