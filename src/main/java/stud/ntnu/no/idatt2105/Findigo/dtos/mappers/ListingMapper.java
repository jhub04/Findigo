package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for mapping between {@link Listing} entities and {@link ListingRequest},
 * {@link ListingResponse} DTOs.
 * <p>
 * This class is responsible for converting {@code ListingRequest} objects into {@code Listing}
 * entities, and
 * {@code Listing} entities into {@code ListingResponse} DTOs. It also handles the mapping of
 * listing attributes
 * from requests and entities.
 * </p>
 */
@NoArgsConstructor
public class ListingMapper {

  /**
   * Converts a {@link ListingRequest} DTO to a {@link Listing} entity.
   * <p>
   * This method maps the request data to a new {@code Listing} entity, associates it with the provided
   * {@link User}, {@link Category}, and a list of {@link Attribute} definitions. It also maps the listing
   * attributes based on the provided attribute definitions.
   * </p>
   *
   * @param request the {@link ListingRequest} DTO containing the listing data
   * @param user the {@link User} associated with the listing
   * @param category the {@link Category} associated with the listing
   * @param attributeDefinitions a list of {@link Attribute} definitions to map the attributes to
   * @return the corresponding {@link Listing} entity
   * @throws RuntimeException if an {@link Attribute} definition cannot be found for an attribute ID
   */
  public static Listing toEntity(ListingRequest request, User user, Category category, List<Attribute> attributeDefinitions) {
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
              .filter(a -> a.getId() == attrReq.getAttributeId())
              .findFirst()
              .orElseThrow(() -> new RuntimeException("Attribute definition not found: " + attrReq.getAttributeId()));

          return ListingAttributeMapper.toEntity(attrDef, attrReq.getValue(), listing);
        })
        .toList();

    listing.setListingAttributes(listingAttributes);
    return listing;
  }

  /**
   * Converts a {@link Listing} entity to a {@link ListingResponse} DTO.
   * <p>
   * This method maps the {@code Listing} entity, along with its attributes and associated entities (such as
   * {@link Category} and {@link User}), to a {@code ListingResponse} DTO suitable for returning to the client.
   * </p>
   *
   * @param listing the {@link Listing} entity to convert
   * @return a {@link ListingResponse} DTO containing the listing details and associated entities
   */
  public static ListingResponse toDto(Listing listing) {
    List<ListingAttributeResponse> attributeResponses = listing.getListingAttributes().stream()
        .map(ListingAttributeMapper::toDto)
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
        new UserLiteResponse(listing.getUser().getId(), listing.getUser().getUsername()),
        attributeResponses,
        listing.getImageUrls().size()
    );
  }
}
