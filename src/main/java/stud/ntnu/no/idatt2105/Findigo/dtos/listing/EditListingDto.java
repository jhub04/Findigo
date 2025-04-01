package stud.ntnu.no.idatt2105.Findigo.dtos.listing;

import lombok.AllArgsConstructor;
import lombok.Data;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;

import java.util.List;

/**
 * DTO for editing a listing, containing details such as descriptions,
 * location, category, images, and attributes.
 */
@Data
@AllArgsConstructor
public class EditListingDto {
  private Long id;
  private String briefDescription;
  private String fullDescription;
  private double latitude;
  private double longitude;
  private Long categoryId;
  private List<String> imageUrls;
  private List<ListingAttributeRequest> attributes;
}