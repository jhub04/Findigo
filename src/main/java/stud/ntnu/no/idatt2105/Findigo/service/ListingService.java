package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.EditListingDto;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingAttributeMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.ListingAttribute;
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

  /**
   * Adds a new listing for a given user.
   *
   * @param req      The listing request containing details of the listing.
   * @return The saved {@link Listing} entity.
   * @throws UsernameNotFoundException if the user is not found in the database.
   * @throws RuntimeException          if the specified category does not exist.
   */
  @Transactional
  public Listing addListing(ListingRequest req) {
    String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepository.findByUsername(currentUsername)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Category category = categoryRepository.findById(req.getCategoryId())
        .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

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
   * @param editListingDto The DTO containing the updated listing details.
   * @return A {@link ListingResponse} containing the updated listing details.
   * @throws NoSuchElementException If the listing or category is not found.
   */
  public ListingResponse editListing(EditListingDto editListingDto) {
    Listing listing = listingRepository.findById(editListingDto.getId())
        .orElseThrow(() -> new NoSuchElementException("No listing found with id " + editListingDto.getId()));

    listing.setBriefDescription(editListingDto.getBriefDescription())
        .setFullDescription(editListingDto.getFullDescription())
        .setLatitude(editListingDto.getLatitude())
        .setLongitude(editListingDto.getLongitude())
        .setCategory(categoryRepository.findById(
                editListingDto.getCategoryId())
            .orElseThrow(() -> new NoSuchElementException("No category with id " + editListingDto.getCategoryId())))

        .setListingAttributes(editListingDto.getAttributes().stream()
            .map(listingAttributeRequest -> listingAttributeMapper.fromRequestToEntity(listingAttributeRequest, editListingDto.getId())).toList())
        .setImageUrls(editListingDto.getImageUrls());

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
    if (!listingRepository.existsById(listingId)) {
      throw new NoSuchElementException("There is no listing with id " + listingId);
    }
    listingRepository.deleteById(listingId);
  }

}
