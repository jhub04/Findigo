package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a category in the system.
 * <p>
 * A category groups listings and defines a set of attributes applicable to its listings.
 * </p>
 */
@Data
@Entity
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
@ToString(exclude = {"attributes", "listings"})
public class Category {

  /**
   * The unique identifier for the category.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the category.
   * Must be unique and cannot be null.
   */
  @Column(nullable = false, unique = true)
  private String categoryName;

  /**
   * Attributes associated with this category.
   * These define additional properties that listings in this category can have.
   * <p>
   * Eagerly fetched, and cascade operations ensure attributes are removed when the category is deleted.
   * </p>
   */
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<Attribute> attributes = new ArrayList<>();

  /**
   * Listings belonging to this category.
   * Listings represent individual items classified under this category.
   * <p>
   * Cascade operations ensure listings are removed when the category is deleted.
   * Lazy fetching optimizes performance.
   * </p>
   */
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Listing> listings = new ArrayList<>();
}
