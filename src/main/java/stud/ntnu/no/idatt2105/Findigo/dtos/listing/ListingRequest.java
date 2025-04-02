package stud.ntnu.no.idatt2105.Findigo.dtos.listing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;

import java.util.List;

/**
 * Represents an authentication request data transfer object containing user credentials.
 * This class is used when a user attempts to log in.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingRequest {
  private String briefDescription;
  private String fullDescription;
  private double latitude;
  private double longitude;
  private double price;
  private String address;
  private String postalCode;
  private Long categoryId;
  private List<ListingAttributeRequest> attributes;

}
