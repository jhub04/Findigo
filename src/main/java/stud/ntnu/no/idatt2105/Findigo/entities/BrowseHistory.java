package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "browse_history")
public class BrowseHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long browse_history_id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;

  @CreationTimestamp
  @Column(updatable = false, name = "created_at")
  private Date createdAt;
}
