package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.FilterListingsRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingAttributeMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.SaleMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.sale.SaleResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.*;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    //GOOD
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

    return listingRepository.findListingsByCategoryIdAndListingStatus(categoryID, ListingStatus.ACTIVE).stream()
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

    List<Listing> listings = listingRepository.findAllByUser_IdNotAndListingStatus(currentUserId, ListingStatus.ACTIVE);

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
   * @param request   The updated listing details.
   * @return A {@link ListingResponse} containing the updated details.
   * @throws AppEntityNotFoundException if the listing or category does not exist.
   * @throws AccessDeniedException      if the current user does not own the listing.
   */
  public ListingResponse editMyListing(Long listingId, ListingRequest request) {
    logger.info("updating with request {}", request);
    Listing listing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    logger.info("listing retrieved {}", listing);
    if (!securityUtil.isListingOwner(listing)) {
      logger.warn("Access denied: User does not own listing ID {}", listingId);
      throw new AccessDeniedException("You do not own this listing");
    }

    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));
    logger.info("category got{}", category);

    listing.getListingAttributes().clear();

    listing.setBriefDescription(request.getBriefDescription())
        .setFullDescription(request.getFullDescription())
        .setLatitude(request.getLatitude())
        .setLongitude(request.getLongitude())
        .setCategory(category)
        .setPrice(request.getPrice())
        .setPostalCode(request.getPostalCode())
        .setAddress(request.getAddress())
        .getListingAttributes().addAll(
            request.getAttributes().stream()
                .map(attr -> listingAttributeMapper.fromRequestToEntity(attr, listing))
                .toList());
    logger.info("NEW listing attributes {}", listing.getListingAttributes());

    Listing updatedListing;
    logger.info("new listing{}", listing);
    try {
      updatedListing = listingRepository.save(listing);
      logger.info("Listing updated successfully with ID {}", listingId);
    } catch (Exception e) {
      logger.error("error in updating listing " + e.getMessage());
      throw e;
    }
    ListingResponse listingResponse = listingMapper.toDto(updatedListing);
    logger.info("Listing response {}", listingResponse);
    return listingResponse;
  }

  /**
   * Edits an existing listing as an administrator.
   * <p>
   * Admins can edit any listing, regardless of ownership.
   *
   * @param listingId The ID of the listing to edit.
   * @param request   The updated listing details.
   * @return A {@link ListingResponse} containing the updated details.
   * @throws AppEntityNotFoundException if the listing or category does not exist.
   */
  public ListingResponse editListingAsAdmin(Long listingId, ListingRequest request) {
    Listing listing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));

    listing.setBriefDescription(request.getBriefDescription())
        .setFullDescription(request.getFullDescription())
        .setLatitude(request.getLatitude())
        .setLongitude(request.getLongitude())
        .setCategory(category)
        .setListingAttributes(request.getAttributes().stream()
            .map(attr -> listingAttributeMapper.fromRequestToEntity(attr, listing))
            .toList());

    Listing updatedListing = listingRepository.save(listing);

    logger.info("Admin updated listing successfully with ID {}", listingId);
    return listingMapper.toDto(updatedListing);
  }

  /**
   * Deletes a listing by its ID.
   *
   * @param listingId The ID of the listing to delete.
   * @throws AppEntityNotFoundException if the listing does not exist.
   * @throws AccessDeniedException      if the current user does not own the listing.
   */
  public void deleteListing(long listingId) {
    logger.info("Deleting listing with ID {}", listingId);

    Listing listing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (!securityUtil.isListingOwner(listing)) {
      logger.warn("Access denied: User does not own listing ID {}", listingId);
      throw new AccessDeniedException("You do not own this listing");
    }

    listingRepository.deleteById(listingId);
    logger.info("Listing deleted successfully with ID {}", listingId);
  }

  /**
   * Deletes a listing by its ID as an administrator.
   * <p>
   * Admins can delete any listing, regardless of ownership.
   *
   * @param listingId The ID of the listing to delete.
   * @throws AppEntityNotFoundException if the listing does not exist.
   */
  public void deleteListingAsAdmin(long listingId) {
    logger.info("Admin: Deleting listing with ID {}", listingId);

    Listing listing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    listingRepository.deleteById(listingId);
    logger.info("Admin: Listing deleted successfully with ID {}", listingId);
  }


  /**
   * Retrieves a paginated list of filtered listings based on the given filter request.
   *
   * @param page                  The page number to retrieve.
   * @param size                  The number of listings per page.
   * @param filterListingsRequest The request containing filter criteria.
   * @return A {@link Page} of {@link ListingResponse} objects matching the filter criteria.
   */
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

  /**
   * Retrieves all listings filtered by the given filter request.
   *
   * @param filterListingsRequest The request containing filter criteria.
   * @return A list of {@link ListingResponse} objects matching the filter criteria.
   */
  public List<ListingResponse> getAllFilteredListings(FilterListingsRequest filterListingsRequest) {
    User currentUser = securityUtil.getCurrentUser();

    List<Listing> listingsToFilter;
    if (filterListingsRequest.getCategoryId() != null) {
      listingsToFilter = listingRepository.findByCategoryIdAndUser_IdNotAndListingStatus(
          filterListingsRequest.getCategoryId(), currentUser.getId(), ListingStatus.ACTIVE);
    } else {
      listingsToFilter = listingRepository.findAllByUser_IdNotAndListingStatus(currentUser.getId(), ListingStatus.ACTIVE);
    }

    Stream<Listing> stream = listingsToFilter.stream();

    if (filterListingsRequest.getQuery() != null) {
      stream = stream.filter(listing -> listing.getBriefDescription().toLowerCase()
          .contains(filterListingsRequest.getQuery().toLowerCase())
          || listing.getFullDescription().toLowerCase()
          .contains(filterListingsRequest.getQuery().toLowerCase()));
    }

    if (filterListingsRequest.getFromPrice() != null) {
      stream = stream.filter(listing -> listing.getPrice() >= filterListingsRequest.getFromPrice());
    }

    if (filterListingsRequest.getToPrice() != null) {
      stream = stream.filter(listing -> listing.getPrice() <= filterListingsRequest.getToPrice());
    }

    if (filterListingsRequest.getFromDate() != null) {
      stream = stream.filter(listing -> listing.getDateCreated()
          .after(filterListingsRequest.getFromDate()));
    }

    List<Listing> filteredListings = stream.toList();

    return filteredListings.stream().map(listingMapper::toDto).toList();
  }

  /**
   * Marks a listing as sold.
   *
   * @param listingId The ID of the listing to mark as sold.
   * @throws AppEntityNotFoundException if the listing does not exist.
   * @throws AccessDeniedException if the current user does not own the listing.
   * @throws IllegalStateException if the listing is already sold or archived.
   */
  public SaleResponse markListingAsSold(long listingId) {
    Listing soldListing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (soldListing.getListingStatus() == ListingStatus.SOLD || soldListing.getListingStatus() == ListingStatus.ARCHIVED) {
      logger.warn("Listing ID {} is already marked as sold or archived", listingId);
      throw new IllegalStateException("Listing is already marked as sold or archived");
    }

    soldListing.setListingStatus(ListingStatus.SOLD);
    listingRepository.save(soldListing);

    Sale sale = new Sale()
        .setListing(soldListing)
        .setSalePrice(soldListing.getPrice());

    return SaleMapper.toDto(sale);
  }

  /**
   * Marks a listing as archived.
   *
   * @param listingId The ID of the listing to mark as archived.
   * @throws AppEntityNotFoundException if the listing does not exist.
   * @throws AccessDeniedException if the current user does not own the listing.
   * @throws IllegalStateException if the listing is already archived or sold.
   */
  public void markListingAsArchived(long listingId) {
    Listing archivedListing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (!securityUtil.isListingOwner(archivedListing)) {
      logger.warn("Access denied: User does not own listing ID {}", listingId);
      throw new AccessDeniedException("You do not own this listing");
    }

    if (archivedListing.getListingStatus() == ListingStatus.ARCHIVED || archivedListing.getListingStatus() == ListingStatus.SOLD) {
      logger.warn("Listing ID {} is already archived or sold", listingId);
      throw new IllegalStateException("Listing is already archived or sold");
    }

    archivedListing.setListingStatus(ListingStatus.ARCHIVED);
    listingRepository.save(archivedListing);

    logger.info("Listing ID {} marked as archived", listingId);
  }

  /**
   * Marks a listing as active.
   *
   * @param listingId The ID of the listing to mark as active.
   * @throws AppEntityNotFoundException if the listing does not exist.
   * @throws AccessDeniedException if the current user does not own the listing.
   * @throws IllegalStateException if the listing is already active or sold.
   */
  public void markListingAsActive(long listingId) {
    Listing activeListing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (!securityUtil.isListingOwner(activeListing)) {
      logger.warn("Access denied: User does not own listing ID {}", listingId);
      throw new AccessDeniedException("You do not own this listing");
    }

    if (activeListing.getListingStatus() == ListingStatus.ACTIVE || activeListing.getListingStatus() == ListingStatus.SOLD) {
      logger.warn("Listing ID {} is already active or sold", listingId);
      throw new IllegalStateException("Listing is already active or sold");
    }

    activeListing.setListingStatus(ListingStatus.ACTIVE);
    listingRepository.save(activeListing);

    logger.info("Listing ID {} marked as active", listingId);
  }


}
