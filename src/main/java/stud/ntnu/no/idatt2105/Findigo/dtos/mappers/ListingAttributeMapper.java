package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.ListingAttribute;
import stud.ntnu.no.idatt2105.Findigo.repository.AttributeRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.util.NoSuchElementException;

/**
 * Service class for mapping between {@link ListingAttribute} entities and their corresponding DTOs.
 * <p>
 * Handles conversion of incoming requests into {@link ListingAttribute} entities
 * and mapping from entities to response DTOs.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ListingAttributeMapper {

  private static final Logger logger = LogManager.getLogger(ListingAttributeMapper.class);

  private final AttributeRepository attributeRepository;
  private final ListingRepository listingRepository;

  /**
   * Converts an {@link Attribute}, a value, and a {@link Listing} into a {@link ListingAttribute} entity.
   *
   * @param attribute the attribute to associate with the listing
   * @param value the value of the attribute
   * @param listing the listing to associate the attribute with
   * @return a new {@link ListingAttribute} entity
   */
  public ListingAttribute toEntity(Attribute attribute, String value, Listing listing) {
    logger.debug("Mapping to ListingAttribute entity for attribute id {}, listing id {}", attribute.getId(), listing.getId());
    return new ListingAttribute()
            .setAttributeValue(value)
            .setAttribute(attribute)
            .setListing(listing);
  }

  /**
   * Converts a {@link ListingAttribute} entity to a {@link ListingAttributeResponse} DTO.
   *
   * @param listingAttribute the entity to convert
   * @return a DTO containing the attribute name and value
   */
  public ListingAttributeResponse toDto(ListingAttribute listingAttribute) {
    logger.debug("Mapping ListingAttribute entity to DTO for attribute id {}", listingAttribute.getAttribute().getId());
    return new ListingAttributeResponse(
            listingAttribute.getAttribute().getAttributeName(),
            listingAttribute.getAttributeValue()
    );
  }

  /**
   * Converts a {@link ListingAttributeRequest} into a {@link ListingAttribute} entity,
   * fetching necessary entities from the database.
   *
   * @param listingAttribute the request DTO containing the attribute ID and value
   * @param listing the listing to associate
   * @return a new {@link ListingAttribute} entity
   * @throws NoSuchElementException if the attribute or listing cannot be found
   */
  public ListingAttribute fromRequestToEntity(ListingAttributeRequest listingAttribute, Listing listing) {
    Attribute attribute = attributeRepository.findById(listingAttribute.getAttributeId())
            .orElseThrow(() -> new NoSuchElementException("No attribute with id " + listingAttribute.getAttributeId()));

    logger.info("Creating ListingAttribute: attributeId={}, attributeName={}, value={}, listingId={}",
            attribute.getId(),
            attribute.getAttributeName(),
            listingAttribute.getValue(),
            listing.getId());

    return new ListingAttribute()
            .setAttribute(attribute)
            .setListing(listing)
            .setAttributeValue(listingAttribute.getValue());
  }

}
