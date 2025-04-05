package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Entity representing a user's favorite listing.
 * <p>
 * Each entry links a user to a favorited listing.
 * A combination of user and listing must be unique.
 * </p>
 */
@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "favorite_listings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "listing_id"})
)
public class FavoriteListings {

  /**
   * Unique identifier for this favorite entry.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  /**
   * The user who marked the listing as favorite.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * The listing that has been marked as favorite.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;
}
