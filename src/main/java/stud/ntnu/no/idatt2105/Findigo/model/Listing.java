package stud.ntnu.no.idatt2105.Findigo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a listing in the system, which is a post created by a user and categorized under a specific category.
 * Each listing can have a brief and full description, and is associated with a user and a category.
 * It can also have a list of custom attributes defined in {@link ListingAttribute}.
 */
@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "listing")
public class Listing {

  /**
   * Unique identifier for the listing.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Short description or summary of the listing.
   * Cannot be null.
   */
  @Column(nullable = false)
  private String briefDescription;

  /**
   * Full, detailed description of the listing.
   * Cannot be null.
   */
  @Column(nullable = false)
  private String fullDescription;

  /**
   * The category this listing belongs to.
   * Cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  /**
   * The user who created the listing.
   * Cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  /**
   * List of attributes associated with this listing.
   * These are custom fields defined per category.
   */
  @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
  private List<ListingAttribute> listingAttributes = new ArrayList<>();

  /**
   *
   */
  @ElementCollection
  @CollectionTable(name = "listing_image_urls", joinColumns = @JoinColumn(name = "listing_id"))
  @Column(name = "image_url")
  private List<String> imageUrls = new ArrayList<>();
}
