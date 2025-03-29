package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
  //TODO javadoc
}
