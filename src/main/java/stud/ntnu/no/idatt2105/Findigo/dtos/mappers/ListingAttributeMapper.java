package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.ListingAttribute;
import stud.ntnu.no.idatt2105.Findigo.repository.AttributeRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.util.NoSuchElementException;

/**
 * A utility class for mapping {@link ListingAttribute} entities to {@link ListingAttributeResponse} DTOs
 * and vice versa. Additionally, it handles the conversion of incoming requests into {@link ListingAttribute} entities.
 * <p>
 * This class utilizes repositories to fetch related {@link Attribute} and {@link Listing} entities when creating
 * or updating {@link ListingAttribute} entities.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ListingAttributeMapper {

  private final AttributeRepository attributeRepository;
  private final ListingRepository listingRepository;

  /**
   * Converts an {@link Attribute} entity, a value, and a {@link Listing} entity into a {@link ListingAttribute} entity.
   *
   * @param attribute the {@link Attribute} to associate with the listing
   * @param value the value for the listing's attribute
   * @param listing the {@link Listing} entity to associate with the attribute
   * @return a new {@link ListingAttribute} entity
   */
  public static ListingAttribute toEntity(Attribute attribute, String value, Listing listing) {
    return new ListingAttribute()
        .setAttributeValue(value)
        .setAttribute(attribute)
        .setListing(listing);
  }

  /**
   * Converts a {@link ListingAttribute} entity to a {@link ListingAttributeResponse} DTO.
   *
   * @param listingAttribute the {@link ListingAttribute} entity to convert
   * @return a {@link ListingAttributeResponse} DTO containing the attribute name and value
   */
  public static ListingAttributeResponse toDto(ListingAttribute listingAttribute) {
    return new ListingAttributeResponse(
        listingAttribute.getAttribute().getAttributeName(),
        listingAttribute.getAttributeValue()
    );
  }

  /**
   * Converts a {@link ListingAttributeRequest} into a {@link ListingAttribute} entity, using the provided
   * listing ID to fetch the corresponding {@link Listing} entity and attribute ID to fetch the corresponding
   * {@link Attribute} entity.
   *
   * @param listingAttribute the {@link ListingAttributeRequest} containing the attribute ID and value
   * @param listingID the ID of the {@link Listing} to associate with the attribute
   * @return a {@link ListingAttribute} entity with the requested attribute and value
   * @throws NoSuchElementException if no {@link Attribute} or {@link Listing} can be found with the provided IDs
   */
  public ListingAttribute fromRequestToEntity(ListingAttributeRequest listingAttribute, long listingID) {
    return new ListingAttribute()
        .setAttribute(attributeRepository.findById(listingAttribute.getAttributeId())
            .orElseThrow(() -> new NoSuchElementException("No attribute with id " + listingAttribute.getAttributeId())))
        .setListing(listingRepository.findById(listingID)
            .orElseThrow(() -> new NoSuchElementException("No listing with id " + listingID)))
        .setAttributeValue(listingAttribute.getValue());
  }
}
