package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing {@link Listing} entities.
 * Extends {@link JpaRepository} to provide CRUD operations.
 */
@Repository

public interface ListingRepository extends JpaRepository<Listing, Long> {
  /**
   * Finds all listings created by a specific user.
   *
   * @param user The user whose listings are to be retrieved.
   * @return A list of listings associated with the given user.
   */
  List<Listing> findListingByUser(User user);

  /**
   * Finds all listings that belong to a specific category.
   *
   * @param id The ID of the category.
   * @return A list of listings belonging to the specified category.
   */
  List<Listing> findListingsByCategoryId(Long id);
  List<Listing> findAllByUser_IdNot(long id);
  Page<Listing> findByCategoryAndIdNotIn(Category category, Set<Long> ids, Pageable pageable);
}
