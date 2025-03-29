package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.ListingAttribute;

@NoArgsConstructor
public class ListingAttributeMapper {

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
}
