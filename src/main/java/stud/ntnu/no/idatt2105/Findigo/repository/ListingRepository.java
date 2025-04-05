package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing {@link Listing} entities.
 * <p>
 * Provides CRUD operations and custom queries for listings,
 * including filtering by user, category, and exclusions.
 * </p>
 */
@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

  /**
   * Finds all listings created by a specific user.
   *
   * @param user the user whose listings are to be retrieved
   * @return a list of listings associated with the given user
   */
  List<Listing> findListingByUser(User user);

  /**
   * Finds all listings that belong to a specific category.
   *
   * @param id the ID of the category
   * @return a list of listings belonging to the specified category
   */
  List<Listing> findListingsByCategoryId(Long id);

  /**
   * Finds all listings not created by the user with the given ID.
   *
   * @param id the ID of the user to exclude
   * @return a list of listings not created by the specified user
   */
  List<Listing> findAllByUser_IdNot(long id);

  /**
   * Finds listings in a given category, excluding listings with IDs in the provided set.
   * Results are paginated.
   *
   * @param category the category of the listings
   * @param ids a set of listing IDs to exclude
   * @param pageable pagination information
   * @return a page of listings matching the criteria
   */
  Page<Listing> findByCategoryAndIdNotIn(Category category, Set<Long> ids, Pageable pageable);
}
