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
 * Represents a category in the system, which can contain multiple attributes and listings.
 * Each category has a unique name and is associated with multiple listings and attributes.
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
   * This ID is automatically generated using the IDENTITY strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the category.
   * This field must be unique and cannot be null.
   */
  @Column(nullable = false, unique = true)
  private String categoryName;

  /**
   * The list of attributes associated with this category.
   * Attributes define additional properties that listings in this category may have.
   * If the category is deleted, all its attributes are also deleted due to CascadeType.ALL.
   */
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Attribute> attributes = new ArrayList<>();

  /**
   * The list of listings associated with this category.
   * Listings represent individual items belonging to this category.
   * If the category is deleted, all its listings are also deleted due to CascadeType.ALL.
   */
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<Listing> listings = new ArrayList<>();
}
