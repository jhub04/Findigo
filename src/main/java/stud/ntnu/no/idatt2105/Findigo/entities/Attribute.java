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
 * Represents an attribute entity that belongs to a specific category.
 * <p>
 * Attributes define additional properties that listings in a category can have,
 * such as color, size, or material.
 * </p>
 */
@Data
@Entity
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Attribute")
@ToString(exclude = "category")
public class Attribute {

  /**
   * The unique identifier for the attribute.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /**
   * The name of the attribute.
   * Cannot be null.
   */
  @Column(nullable = false)
  private String attributeName;

  /**
   * The data type of the attribute (e.g., String, Integer, Boolean).
   * Cannot be null.
   */
  @Column(nullable = false)
  private String dataType;

  /**
   * The category this attribute belongs to.
   * Defined as many-to-one with lazy loading.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  /**
   * The listing attributes associated with this attribute.
   * Cascade and orphan removal are enabled.
   */
  @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ListingAttribute> listingAttributes = new ArrayList<>();
}
