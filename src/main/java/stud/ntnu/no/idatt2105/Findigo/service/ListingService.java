package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for managing listings.
 * <p>
 * This service provides functionality for adding new listings and retrieving user-specific listings.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ListingService {

  private final ListingRepository listingRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  /**
   * Adds a new listing for a given user.
   *
   * @param username The username of the user adding the listing.
   * @param req      The listing request containing details of the listing.
   * @return The saved {@link Listing} entity.
   * @throws UsernameNotFoundException if the user is not found in the database.
   * @throws RuntimeException          if the specified category does not exist.
   */
  @Transactional
  public Listing addListing(String username, ListingRequest req) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Category category = categoryRepository.findById(req.getCategoryId())
        .orElseThrow(() -> new RuntimeException("Category not found"));

    Listing listing = ListingMapper.toEntity(
        req,
        user,
        category,
        category.getAttributes()
    );

    return listingRepository.save(listing);
  }

  /**
   * Retrieves all listings associated with a specific user.
   *
   * @param username The username of the user whose listings are to be retrieved.
   * @return A list of {@link ListingResponse} objects containing listing details.
   * @throws UsernameNotFoundException if the user is not found in the database.
   */
  @Transactional
  public List<ListingResponse> getUserListings(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    List<Listing> listings = listingRepository.findListingByUser(user);

    return listings.stream()
        .map(ListingMapper::toDto)
        .toList();
  }

  /**
   * Retrieves all listings associated with a specific cateory.
   *
   * @param categoryID The category id of the category whose listings are to be retrieved.
   * @return A list of {@link ListingResponse} objects containing listing details.
   * @throws NoSuchElementException if there are no listings associated with the given category.
   */
  public List<ListingResponse> getListingsInCategory(Long categoryID) {
    List<Listing> listings = listingRepository.findListingsByCategoryId(categoryID);
    if (listings.isEmpty()) {
      throw new NoSuchElementException("Couldn't find any listings in category with ID "+categoryID);
    }
    return listings.stream()
        .map(ListingMapper::toDto).toList();
  }

  /**
   * Retrieves all listings in database.
   *
   * @return A list of {@link ListingResponse} objects containing listing details.
   * @throws NoSuchElementException if there are no listings in database.
   */
  public List<ListingResponse> getAllListings() {
    List<Listing> listings = listingRepository.findAll();
    if (listings.isEmpty()) {
      throw new NoSuchElementException("Couldn't find any listings in database");
    }
    return listings.stream()
        .map(ListingMapper::toDto).toList();
  }
}
