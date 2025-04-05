package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.*;
import java.util.List;

/**
 * Service for mapping between {@link Listing} entities and their corresponding DTOs.
 * <p>
 * Responsible for converting {@link ListingRequest} objects into {@link Listing} entities,
 * and {@link Listing} entities into {@link ListingResponse} DTOs for API responses.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ListingMapper {

  private static final Logger logger = LogManager.getLogger(ListingMapper.class);
  private final ListingAttributeMapper listingAttributeMapper;

  /**
   * Converts a {@link ListingRequest} DTO to a {@link Listing} entity.
   *
   * @param request the {@link ListingRequest} DTO containing the listing data
   * @param user the {@link User} associated with the listing
   * @param category the {@link Category} associated with the listing
   * @param attributeDefinitions a list of {@link Attribute} definitions to map the attributes to
   * @return the corresponding {@link Listing} entity
   * @throws RuntimeException if an {@link Attribute} definition cannot be found for an attribute ID
   */
  public Listing toEntity(ListingRequest request, User user, Category category, List<Attribute> attributeDefinitions) {
    logger.debug("Mapping ListingRequest to Listing entity for user '{}'", user.getUsername());

    Listing listing = new Listing()
            .setBriefDescription(request.getBriefDescription())
            .setFullDescription(request.getFullDescription())
            .setLatitude(request.getLatitude())
            .setLongitude(request.getLongitude())
            .setCategory(category)
            .setUser(user)
            .setPrice(request.getPrice())
            .setAddress(request.getAddress())
            .setPostalCode(request.getPostalCode());

    List<ListingAttribute> listingAttributes = request.getAttributes().stream()
            .map(attrReq -> {
              Attribute attrDef = attributeDefinitions.stream()
                      .filter(a -> a.getId() == (attrReq.getAttributeId()))
                      .findFirst()
                      .orElseThrow(() -> new RuntimeException("Attribute definition not found: " + attrReq.getAttributeId()));

              return listingAttributeMapper.toEntity(attrDef, attrReq.getValue(), listing);
            })
            .toList();

    listing.setListingAttributes(listingAttributes);

    logger.info("Mapped ListingRequest to Listing entity with id {}", listing.getId());
    return listing;
  }

  /**
   * Converts a {@link Listing} entity to a {@link ListingResponse} DTO.
   *
   * @param listing the {@link Listing} entity to convert
   * @return a {@link ListingResponse} DTO containing the listing details and associated entities
   */
  public ListingResponse toDto(Listing listing) {
    logger.debug("Mapping Listing entity with id {} to ListingResponse DTO", listing.getId());

    List<ListingAttributeResponse> attributeResponses = listing.getListingAttributes().stream()
            .map(listingAttributeMapper::toDto)
            .toList();

    return new ListingResponse(
            listing.getId(),
            listing.getBriefDescription(),
            listing.getFullDescription(),
            listing.getLatitude(),
            listing.getLongitude(),
            listing.getPrice(),
            listing.getAddress(),
            listing.getPostalCode(),
            listing.getDateCreated(),
            CategoryMapper.toDto(listing.getCategory()),
            new UserLiteResponse(listing.getUser().getId(), listing.getUser().getUsername(), listing.getUser().getPhoneNumber()),
            attributeResponses,
            listing.getImageUrls().size()
    );
  }
}
