package stud.ntnu.no.idatt2105.Findigo.dtos.listing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;

import java.util.List;

/**
 * DTO for creating or editing a listing.
 * <p>
 * Contains details about the listing, such as descriptions, location, price, category, and attributes.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO for creating or editing a listing with details like description, location, price, category, and attributes.")
public class ListingRequest {

  /**
   * A brief description of the listing.
   */
  @NotBlank(message = "Brief description must not be blank")
  @Schema(description = "Brief description of the listing", example = "Vintage bicycle in good condition")
  private String briefDescription;

  /**
   * A full detailed description of the listing.
   */
  @NotBlank(message = "Full description must not be blank")
  @Schema(description = "Detailed description of the listing", example = "Fully refurbished vintage bicycle from the 1980s with new tires and seat.")
  private String fullDescription;

  /**
   * The latitude of the listing location.
   */
  @Schema(description = "Latitude of the listing location", example = "63.4305")
  private double latitude;

  /**
   * The longitude of the listing location.
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
  @NotBlank(message = "Address must not be blank")
  @Schema(description = "Address of the listing", example = "Olav Tryggvasons gate 24")
  private String address;

  /**
   * The postal code of the listing.
   */
  @NotBlank(message = "Postal code must not be blank")
  @Schema(description = "Postal code of the listing", example = "7011")
  private String postalCode;

  /**
   * The category ID to which the listing belongs.
   */
  @NotNull(message = "Category ID must not be null")
  @Schema(description = "Category ID of the listing", example = "3")
  private Long categoryId;

  /**
   * List of attributes associated with the listing.
   */
  @NotEmpty(message = "Attributes list must not be empty")
  @Schema(description = "List of attributes associated with the listing")
  private List<ListingAttributeRequest> attributes;
}
