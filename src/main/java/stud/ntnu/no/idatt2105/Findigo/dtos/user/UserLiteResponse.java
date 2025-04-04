package stud.ntnu.no.idatt2105.Findigo.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;

import java.util.List;

/**
 * Represents an authentication request data transfer object containing user credentials.
 * This class is used when a user attempts to log in.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLiteResponse {
  private Long id;
  private String username;
  private Long phoneNumber;
}
