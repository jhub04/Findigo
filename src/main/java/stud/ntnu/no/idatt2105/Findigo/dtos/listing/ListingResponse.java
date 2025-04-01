package stud.ntnu.no.idatt2105.Findigo.dtos.listing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;

import java.util.List;

/**
 * Represents an authentication request data transfer object containing user credentials.
 * This class is used when a user attempts to log in.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingResponse {
  private Long id;
  private String briefDescription;
  private String fullDescription;
  private double latitude;
  private double longitude;
  private CategoryResponse category;
  private UserResponse user;
  private List<ListingAttributeResponse> attributes;
}
