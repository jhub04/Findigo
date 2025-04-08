package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 * Entity representing a user's browsing history.
 * <p>
 * Tracks which listings have been viewed by users, along with the timestamp.
 * </p>
 */
@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "browse_history")
@ToString(exclude = {"user", "listing"})
public class BrowseHistory {

  /**
   * The unique identifier for the browse history entry.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "browse_history_id")
  private Long browseHistoryId;

  /**
   * The user who viewed the listing.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * The listing that was viewed.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;

  /**
   * The timestamp when the browse history entry was created.
   */
  @CreationTimestamp
  @Column(updatable = false, name = "created_at")
  private Date createdAt;
}
