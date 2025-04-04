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
    name = "favoriteListings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "listingId"})
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
  @JoinColumn(name = "userId", nullable = false)
  private User user;
  /**
   * The listing that was favorited.
   * Cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "listingId", nullable = false)
  private Listing listing;
}
