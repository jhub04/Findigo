  package stud.ntnu.no.idatt2105.Findigo.entities;

  import jakarta.persistence.*;
  import lombok.*;
  import lombok.experimental.Accessors;
  import org.hibernate.annotations.CreationTimestamp;

  import java.util.ArrayList;
  import java.util.Date;
  import java.util.List;

  /**
   * Entity representing a listing in the system.
   * <p>
   * Each listing is created by a user, belongs to a specific category,
   * and can contain custom attributes and images.
   * </p>
   */
  @Getter
  @Setter
  @Entity
  @Accessors(chain = true)
  @NoArgsConstructor
  @AllArgsConstructor
  @Table(name = "listing")
  @ToString(exclude = {"category", "user", "listingAttributes", "imageUrls", "browseHistories", "favoriteListings"})
  public class Listing {

    /**
     * Unique identifier for the listing.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short description or summary of the listing.
     */
    @Column(nullable = false)
    private String briefDescription;

    /**
     * Detailed description of the listing.
     */
    @Column(nullable = false)
    private String fullDescription;

    /**
     * Longitude of the listing location.
     */
    @Column(nullable = false)
    private double longitude;

    /**
     * Latitude of the listing location.
     */
    @Column(nullable = false)
    private double latitude;

    /**
     * Price of the listing.
     */
    @Column(nullable = false)
    private double price;

    /**
     * Address of the listing.
     */
    @Column(nullable = false)
    private String address;

    /**
     * Postal code of the listing.
     */
    @Column(nullable = false)
    private String postalCode;

    /**
     * Timestamp of when the listing was created.
     * Automatically populated at creation.
     */
    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private Date dateCreated;

    @Column(name = "listing_status")
    @Enumerated(EnumType.STRING)
    private ListingStatus listingStatus = ListingStatus.ACTIVE;

    /**
     * The category this listing belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * The user who created the listing.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    /**
     * List of attributes associated with this listing.
     * These are custom fields defined per category.
     */
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListingAttribute> listingAttributes = new ArrayList<>();

    /**
     * List of image URLs associated with this listing.
     */
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListingImageUrls> imageUrls = new ArrayList<>();

    /**
     * Browsing history entries associated with this listing.
     */
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BrowseHistory> browseHistories = new ArrayList<>();

    /**
     * Favourites associated with this listing.
     */
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteListings> favoriteListings = new ArrayList<>();

    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private Sale sale;
  }
