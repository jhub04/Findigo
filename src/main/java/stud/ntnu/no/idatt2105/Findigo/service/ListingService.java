package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
 * This service provides functionality for adding new listings and retrieving user-specific listings.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ListingService {

  private final ListingRepository listingRepository;
  private final CategoryRepository categoryRepository;
  private final ListingAttributeMapper listingAttributeMapper;
  private final SecurityUtil securityUtil;
  private final RecommendationService recommendationService;

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
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));

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
   * Retrieves all listings associated with a specific cateory.
   *
   * @param categoryID The category id of the category whose listings are to be retrieved.
   * @return A list of {@link ListingResponse} objects containing listing details.
   * @throws NoSuchElementException if there are no listings associated with the given category.
   */
  public List<ListingResponse> getListingsInCategory(Long categoryID) {
    return listingRepository.findListingsByCategoryId(categoryID).stream() //TODO ikke få med currentusers egne listings her
        .map(ListingMapper::toDto).toList();
  }

  /**
   * Retrieves all listings in database.
   *
   * @return A list of {@link ListingResponse} objects containing listing details.
   * @throws NoSuchElementException if there are no listings in database.
   */
  public List<ListingResponse> getAllListings() {
    List<Listing> listings = listingRepository.findAllByUser_IdNot(securityUtil.getCurrentUser().getId());

    return listings.stream()
        .map(ListingMapper::toDto).toList();
  }

  public ListingResponse getListingById(Long id) {
    Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));
    recommendationService.addListingToBrowseHistory(listing);
    return ListingMapper.toDto(listing);
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
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (securityUtil.isListingOwner(listing)) {
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
                    .toList()); //TODO: legg til nye bilder

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
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (securityUtil.isListingOwner(listing)) {
      throw new AccessDeniedException("You do not own this listing");
    }

    listingRepository.deleteById(listingId);
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

    for (Listing listing : listingsToFilter) {
      if (listing.getBriefDescription().toLowerCase().contains(filterListingsRequest.getQuery().toLowerCase())) {
        filteredListings.add(listing);
      }
    }
    //TODO implement rest of method
    return null;
  }

}
