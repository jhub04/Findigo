package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;

import java.util.Optional;

/**
 * Repository interface for managing {@link Category} entities.
 * <p>
 * Provides CRUD operations and custom queries related to categories.
 * </p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  /**
   * Finds a category by its name.
   *
   * @param categoryName the name of the category
   * @return an {@link Optional} containing the category if found, or empty otherwise
   */
  Optional<Category> findByCategoryName(String categoryName);

  /**
   * Checks if a category with the given name already exists.
   *
   * @param categoryName the name of the category to check
   * @return {@code true} if a category with the given name exists, otherwise {@code false}
   */
  boolean existsByCategoryName(String categoryName);
}
