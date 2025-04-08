package stud.ntnu.no.idatt2105.Findigo.dtos.listing;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;

import java.util.Date;
import java.util.List;
import stud.ntnu.no.idatt2105.Findigo.entities.ListingStatus;

/**
 * Data Transfer Object (DTO) representing a listing response.
 * <p>
 * Contains detailed information about a listing, including descriptions,
 * location, price, creation date, category, user details, attributes, and image count.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing detailed information about a listing.")
public class ListingResponse {

  /**
   * The unique identifier of the listing.
   */
  @Schema(description = "Unique identifier of the listing", example = "101")
  private Long id;

  /**
   * A brief description of the listing.
   */
  @Schema(description = "Brief description of the listing", example = "Vintage bicycle")
  private String briefDescription;

  /**
   * A detailed description of the listing.
   */
  @Schema(description = "Detailed description of the listing", example = "Well-maintained vintage bicycle from the 1980s.")
  private String fullDescription;

  /**
   * The latitude of the listing's location.
   */
  @Schema(description = "Latitude of the listing location", example = "63.4305")
  private double latitude;

  /**
   * The longitude of the listing's location.
   */
  @Schema(description = "Longitude of the listing location", example = "10.3951")
  private double longitude;

  /**
   * The price of the listing.
   */
  @Schema(description = "Price of the listing", example = "1500.00")
  private double price;

  /**
   * The address of the listing.
   */
  @Schema(description = "Address of the listing", example = "Olav Tryggvasons gate 24")
  private String address;

  /**
   * The postal code of the listing.
   */
  @Schema(description = "Postal code of the listing", example = "7011")
  private String postalCode;

  /**
   * The creation date of the listing.
   */
  @Schema(description = "Creation date of the listing (ISO 8601 format)", example = "2024-04-05T12:00:00Z")
  private Date dateCreated;

  /**
   * The status of the listing. Either ACTIVE, ARCHIVED or SOLD
   */
  @Schema(description = "Status of the listing", example = "ACTIVE")
  private ListingStatus listingStatus;

  /**
   * The category associated with the listing.
   */
  @Schema(description = "Category associated with the listing")
  private CategoryResponse category;

  /**
   * The user who created the listing.
   */
  @Schema(description = "User who created the listing")
  private UserLiteResponse user;

  /**
   * The list of attributes associated with the listing.
   */
  @Schema(description = "List of attributes for the listing")
  private List<ListingAttributeResponse> attributes;

  /**
   * The number of images associated with the listing.
   */
  @Schema(description = "Number of images linked to the listing", example = "3")
  private int numberOfImages;
}
