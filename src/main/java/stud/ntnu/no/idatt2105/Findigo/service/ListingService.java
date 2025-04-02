package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingAttributeMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.CategoryNotFoundException;
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
  private final ListingAttributeMapper listingAttributeMapper;
  private final SecurityUtil securityUtil;

  /**
   * Adds a new listing for a given user.
   *
   * @param req      The listing request containing details of the listing.
   * @return The saved {@link Listing} entity.
   * @throws UsernameNotFoundException if the user is not found in the database.
   * @throws RuntimeException          if the specified category does not exist.
   */
  @Transactional
  public ListingResponse addListing(ListingRequest req) {
    User currentUser = securityUtil.getCurrentUser();

    Category category = categoryRepository.findById(req.getCategoryId())
        .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

    Listing listing = ListingMapper.toEntity(
        req,
        currentUser,
        category,
        category.getAttributes()
    );

    Listing savedListing = listingRepository.save(listing);

    return ListingMapper.toDto(savedListing);
  }

  /**
   * Retrieves all listings associated with a specific user.
   *
   * @return A list of {@link ListingResponse} objects containing listing details.
   * @throws UsernameNotFoundException if the user is not found in the database.
   */
  @Transactional
  public List<ListingResponse> getOwnListings() {
    User user = securityUtil.getCurrentUser();

    return user.getListings().stream()
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

  public ListingResponse getListingById(Long id) {
    return listingRepository.findById(id)
            .map(ListingMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("Could not find listing by id"));
  }


  /**
   * Edits an existing listing in the database.
   * <p>
   * This method updates the details of an existing listing, including its description,
   * location, category, attributes, and image URLs.
   * </p>
   *
   * @return A {@link ListingResponse} containing the updated listing details.
   * @throws NoSuchElementException If the listing or category is not found.
   */
  public ListingResponse editListing(Long listingId, ListingRequest request) { //kan opprette editAsAdmin metode
    Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new NoSuchElementException("No listing found with id {}" + listingId));

    if (securityUtil.isListingOwner(listing)) {
      throw new AccessDeniedException("You do not own this listing");
    }

    Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NoSuchElementException("No category found with ID: " + request.getCategoryId()));

    listing.setBriefDescription(request.getBriefDescription())
            .setFullDescription(request.getFullDescription())
            .setLatitude(request.getLatitude())
            .setLongitude(request.getLongitude())
            .setCategory(category)
            .setListingAttributes(request.getAttributes().stream()
                    .map(attr -> listingAttributeMapper.fromRequestToEntity(attr, listingId))
                    .toList())
            .setImageUrls(request.getImageUrls());

    listingRepository.save(listing);

    return ListingMapper.toDto(listing);
  }

  /**
   * Deletes a listing from the database.
   * <p>
   * If the listing exists, it will be removed; otherwise, an exception is thrown.
   * </p>
   *
   * @param listingId The ID of the listing to delete.
   * @throws NoSuchElementException If no listing with the given ID exists.
   */
  public void deleteListing(long listingId) {
    Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new NoSuchElementException("No listing found with id {}" + listingId));

    if (securityUtil.isListingOwner(listing)) {
      throw new AccessDeniedException("You do not own this listing");
    }

    listingRepository.deleteById(listingId);
  }

}
