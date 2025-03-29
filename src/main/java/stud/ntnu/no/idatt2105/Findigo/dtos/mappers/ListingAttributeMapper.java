package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class ListingAttributeMapper {
  private final AttributeRepository attributeRepository;
  private final ListingRepository listingRepository;
  //TODO javadoc

  public static ListingAttribute toEntity(Attribute attribute, String value, Listing listing) {
    return new ListingAttribute()
            .setAttributeValue(value)
            .setAttribute(attribute)
            .setListing(listing);
  }

  public static ListingAttributeResponse toDto(ListingAttribute listingAttribute) {
    return new ListingAttributeResponse(
            listingAttribute.getAttribute().getAttributeName(),
            listingAttribute.getAttributeValue()
    );
  }

  public ListingAttribute fromRequestToEntity(ListingAttributeRequest listingAttribute, long listingID) {
    return new ListingAttribute()
        .setAttribute(attributeRepository.findById(listingAttribute.getAttributeId())
            .orElseThrow(()-> new NoSuchElementException("No attribute with id " + listingAttribute.getAttributeId())))
        .setListing(listingRepository.findById(listingID)
            .orElseThrow(()-> new NoSuchElementException("No lisitng with id " + listingID)))
        .setAttributeValue(listingAttribute.getValue());

  }
}
