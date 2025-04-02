package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing {@link Category} entities from the database.
 * Extends {@link JpaRepository} to provide CRUD operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Optional<Category> findByCategoryName(String categoryName);

  boolean existsByCategoryName(String categoryName);
}
