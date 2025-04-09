package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 * Entity representing a sale in the system.
 * <p>
 * A sale is created when a listing is sold, capturing transaction details such as
 * the buyer, the price at the time of sale, and the date of the transaction.
 * </p>
 */
@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sale")
public class Sale {

  /**
   * Unique identifier for the sale transaction.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The listing that was sold.
   */
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;

  /**
   * The price at which the listing was sold.
   */
  @Column(nullable = true)
  private double salePrice;

  /**
   * The date and time when the sale was completed.
   */
  @CreationTimestamp
  @Column(updatable = false, name = "sale_date")
  private Date saleDate;
}
