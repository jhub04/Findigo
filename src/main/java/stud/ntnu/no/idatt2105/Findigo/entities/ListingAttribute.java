package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents an association between a listing and an attribute.
 * This entity stores the specific value of an attribute for a given listing.
 */
@Data
@Entity
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "listing_attribute")
public class ListingAttribute {

  /**
   * The unique identifier for this listing-attribute association.
   * This ID is automatically generated using the IDENTITY strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long Id;

  /**
   * The value assigned to the attribute for this specific listing.
   * This field is required and cannot be null.
   */
  @Column(nullable = false)
  private String attributeValue;

  /**
   * The listing to which this attribute value belongs.
   * This relationship is managed using a many-to-one mapping.
   * The listing_id column is a foreign key and cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;

  /**
   * The attribute associated with this listing.
   * This relationship is managed using a many-to-one mapping.
   * The attribute_id column is a foreign key and cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "attribute_id", nullable = false)
  private Attribute attribute;
}
