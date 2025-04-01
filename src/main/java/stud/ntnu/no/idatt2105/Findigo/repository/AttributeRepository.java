package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

/**
 * Repository interface for accessing {@link Attribute} entities from the database.
 * Extends {@link JpaRepository} to provide CRUD operations.
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
}
