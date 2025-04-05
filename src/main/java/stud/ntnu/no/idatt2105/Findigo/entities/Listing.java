package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.util.*;

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
   * Longitude of the place of the listing
   */
  @Column(nullable = false)
  private double longitude;

  /**
   * Longitude of the place of the listing
   */
  @Column(nullable = false)
  private double latitude;

  /**
   * Price of the listing
   */
  @Column(nullable = false)
  private double price;

  /**
   * Address of the listing
   */
  @Column (nullable = false)
  private String address;

  /**
   * Postal code of the listing
   */
  @Column (nullable = false)
  private String postalCode;

/**
   * The date and time when the listing was created.
   * Automatically set to the current date and time when the listing is created.
   */
@CreationTimestamp
@Column(name = "date_created", updatable = false)
private Date dateCreated;

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
  @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ListingAttribute> listingAttributes = new ArrayList<>();

  /**
   * A list of image URLs associated with the listing.
   *
   * <p>This field is mapped to a separate table named {@code listing_image_urls},
   * where each URL is stored as a row tied to the listing via the {@code listing_id} foreign key.
   * The column storing the actual URL string is named {@code image_url}.</p>
   *
   * <p>Used to represent images for the listing without storing binary data directly in the database.</p>
   */
  @ElementCollection
  @CollectionTable(name = "listing_image_urls", joinColumns = @JoinColumn(name = "listing_id"))
  @Column(name = "image_url")
  private List<String> imageUrls = new ArrayList<>();


  /**
   * Adds an image url to the listing.
   *
   * @param url The url to add.
   */
  public void addImageUrl(String url) {
    imageUrls.add(url);
  }
}
