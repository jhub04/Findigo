package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.ListingImageUrls;

import java.util.List;

/**
 * Repository interface for managing {@link ListingImageUrls} entities.
 * <p>
 * Provides CRUD operations and custom queries for handling image URLs associated with listings.
 * </p>
 */
@Repository
public interface ListingImageRepository extends JpaRepository<ListingImageUrls, Long> {

  /**
   * Finds all image URLs associated with a specific listing.
   *
   * @param listingId the ID of the listing
   * @return a list of {@link ListingImageUrls} linked to the given listing
   */
  List<ListingImageUrls> findByListingId(Long listingId);
}
