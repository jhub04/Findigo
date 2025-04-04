package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  /**
   * The user who favorited the listing.
   * Cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  /**
   * The listing that was favorited.
   * Cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;
}
