package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;

/**
 * Repository interface for managing {@link Attribute} entities.
 * <p>
 * Provides CRUD operations and custom queries related to attributes.
 * </p>
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

  /**
   * Checks if an attribute with the given name exists in the database.
   *
   * @param attributeName the name of the attribute to check
   * @return {@code true} if an attribute with the given name exists, otherwise {@code false}
   */
  boolean existsByAttributeName(String attributeName);
}
