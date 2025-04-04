package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.idatt2105.Findigo.entities.FavoriteListings;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;
import java.util.Optional;

public interface FavoriteListingsRepository extends JpaRepository<FavoriteListings, Long> {
  /**
   * Finds a favorite listing by user ID and listing ID.
   */
  Optional<FavoriteListings> findByUserAndListing(User user, Listing listing);
  List<FavoriteListings> findAllByUser(User user);
}
