package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Entity representing the association between a listing and an attribute.
 * <p>
 * Stores the specific value of an attribute for a given listing.
 * </p>
 */
@Getter
@Setter
@Entity
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "listing_attribute")
public class ListingAttribute {

  /**
   * Unique identifier for this listing-attribute association.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /**
   * Value assigned to the attribute for this specific listing.
   */
  @Column(name = "attribute_value", nullable = false)
  private String attributeValue;

  /**
   * Listing to which this attribute value belongs.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;

  /**
   * Attribute associated with this listing.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attribute_id", nullable = false)
  private Attribute attribute;
}
