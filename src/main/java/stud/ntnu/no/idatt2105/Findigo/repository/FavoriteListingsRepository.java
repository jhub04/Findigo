package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.FavoriteListings;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link FavoriteListings} entities.
 * <p>
 * Provides CRUD operations and custom queries related to user favorite listings.
 * </p>
 */
@Repository
public interface FavoriteListingsRepository extends JpaRepository<FavoriteListings, Long> {

  /**
   * Finds a favorite listing by user and listing.
   *
   * @param user the user who favorited the listing
   * @param listing the listing that was favorited
   * @return an {@link Optional} containing the favorite listing if found, or empty otherwise
   */
  Optional<FavoriteListings> findByUserAndListing(User user, Listing listing);

  /**
   * Retrieves all favorite listings for a specific user.
   *
   * @param user the user whose favorite listings are to be retrieved
   * @return a list of {@link FavoriteListings} for the given user
   */
  List<FavoriteListings> findAllByUser(User user);
}
