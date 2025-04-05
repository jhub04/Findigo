package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Entity representing image URLs associated with a listing.
 * <p>
 * Each entry corresponds to a single image URL linked to a listing.
 * </p>
 */
@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "listing_image_urls")
public class ListingImageUrls {

  /**
   * Unique identifier for the image URL entry.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  /**
   * URL of the image.
   */
  @Column(nullable = false, name = "image_url")
  private String imageUrl;

  /**
   * Listing to which this image belongs.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;
}
