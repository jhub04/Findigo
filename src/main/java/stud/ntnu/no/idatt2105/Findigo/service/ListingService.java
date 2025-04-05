package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.FilterListingsRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingAttributeMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for managing listings.
 * <p>
 * Provides functionalities for creating, retrieving, updating, and deleting listings,
 * including operations specific to categories and user-based filtering.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ListingService {

  private static final Logger logger = LogManager.getLogger(ListingService.class);

  private final ListingRepository listingRepository;
  private final CategoryRepository categoryRepository;
  private final ListingAttributeMapper listingAttributeMapper;
  private final SecurityUtil securityUtil;
  private final RecommendationService recommendationService;
  private final ListingMapper listingMapper;

  /**
   * Adds a new listing for the currently authenticated user.
   *
   * @param req The listing request containing listing details.
   * @return A {@link ListingResponse} with the created listing details.
   * @throws AppEntityNotFoundException if the category does not exist.
   */
  @Transactional
  public ListingResponse addListing(ListingRequest req) {
    User currentUser = securityUtil.getCurrentUser();
    logger.info("Creating listing for user ID {}", currentUser.getId());

    Category category = categoryRepository.findById(req.getCategoryId())
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));

    Listing listing = listingMapper.toEntity(req, currentUser, category, category.getAttributes());

    Listing savedListing = listingRepository.save(listing);

    logger.info("Listing created successfully with ID {}", savedListing.getId());
    return listingMapper.toDto(savedListing);
  }

  /**
   * Retrieves all listings in a specific category.
   *
   * @param categoryID The category id of the category whose listings are to be retrieved.
   * @return A list of {@link ListingResponse} objects containing listing details.
   * @throws NoSuchElementException if there are no listings associated with the given category.
   */
  public List<ListingResponse> getListingsInCategory(Long categoryID) {
    logger.info("Fetching listings for category ID {}", categoryID);

    return listingRepository.findListingsByCategoryId(categoryID).stream()
            .map(listingMapper::toDto)
            .toList();
  }

  /**
   * Retrieves all listings excluding the current user's own listings.
   *
   * @return A list of {@link ListingResponse} objects.
   */
  public List<ListingResponse> getAllListings() {
    long currentUserId = securityUtil.getCurrentUser().getId();
    logger.info("Fetching all listings excluding user ID {}", currentUserId);

    List<Listing> listings = listingRepository.findAllByUser_IdNot(currentUserId);

    return listings.stream()
            .map(listingMapper::toDto)
            .toList();
  }

  /**
   * Retrieves a listing by its ID and logs the view in browse history.
   *
   * @param id The listing ID.
   * @return A {@link ListingResponse} of the listing.
   * @throws AppEntityNotFoundException if the listing does not exist.
   */
  public ListingResponse getListingById(Long id) {
    logger.info("Fetching listing by ID {}", id);

    Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    recommendationService.addListingToBrowseHistory(listing);

    return listingMapper.toDto(listing);
  }

  /**
   * Edits an existing listing.
   *
   * @param listingId The ID of the listing to edit.
   * @param request The updated listing details.
   * @return A {@link ListingResponse} containing the updated details.
   * @throws AppEntityNotFoundException if the listing or category does not exist.
   * @throws AccessDeniedException if the current user does not own the listing.
   */
  public ListingResponse editListing(Long listingId, ListingRequest request) {
    logger.info("Editing listing with ID {}", listingId);

    Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (securityUtil.isListingOwner(listing)) {
      logger.warn("Access denied: User does not own listing ID {}", listingId);
      throw new AccessDeniedException("You do not own this listing");
    }

    Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));

    listing.setBriefDescription(request.getBriefDescription())
            .setFullDescription(request.getFullDescription())
            .setLatitude(request.getLatitude())
            .setLongitude(request.getLongitude())
            .setCategory(category)
            .setListingAttributes(request.getAttributes().stream()
                    .map(attr -> listingAttributeMapper.fromRequestToEntity(attr, listingId))
                    .toList());

    Listing updatedListing = listingRepository.save(listing);

    logger.info("Listing updated successfully with ID {}", listingId);
    return listingMapper.toDto(updatedListing);
  }

  /**
   * Deletes a listing by its ID.
   *
   * @param listingId The ID of the listing to delete.
   * @throws AppEntityNotFoundException if the listing does not exist.
   * @throws AccessDeniedException if the current user does not own the listing.
   */
  public void deleteListing(long listingId) {
    logger.info("Deleting listing with ID {}", listingId);

    Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (securityUtil.isListingOwner(listing)) {
      logger.warn("Access denied: User does not own listing ID {}", listingId);
      throw new AccessDeniedException("You do not own this listing");
    }

    listingRepository.deleteById(listingId);
    logger.info("Listing deleted successfully with ID {}", listingId);
  }

  public Page<ListingResponse> getFilteredListings(int page, int size, FilterListingsRequest filterListingsRequest) {
    List<ListingResponse> filteredListings = getAllFilteredListings(filterListingsRequest);

    int start = Math.min(page * size, filteredListings.size());
    int end = Math.min(start + size, filteredListings.size());

    if (start > filteredListings.size() || start < 0 || end > filteredListings.size() || end < 0) {
      throw new IllegalArgumentException("Invalid page or size parameters");
    }

    List<ListingResponse> pagedListings = filteredListings.subList(start, end);
    return new PageImpl<>(pagedListings, PageRequest.of(page, size), filteredListings.size());
  }

  public List<ListingResponse> getAllFilteredListings(FilterListingsRequest filterListingsRequest) {
    User currentUser = securityUtil.getCurrentUser();
    List<Listing> listingsToFilter;
    if (filterListingsRequest.getCategoryId() != null) {
      //categoryId is a number here
      listingsToFilter = listingRepository.findByCategoryIdAndUser_IdNot(filterListingsRequest.getCategoryId(), currentUser.getId());
    } else {
      listingsToFilter = listingRepository.findAllByUser_IdNot(currentUser.getId());
    }
    List<Listing> filteredListings = new ArrayList<>();
    //Legg til i filtered listings her
    for (Listing listing : listingsToFilter) {
      if (listing.getBriefDescription().toLowerCase().contains(filterListingsRequest.getQuery().toLowerCase())) {
        filteredListings.add(listing);
      }
    }
    return filteredListings.stream().map(listingMapper::toDto).toList();
  }

}
