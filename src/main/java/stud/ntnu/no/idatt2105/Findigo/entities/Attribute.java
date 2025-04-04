package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Represents an attribute that belongs to a specific category.
 * Attributes define additional properties that listings in a category can have.
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
   * This ID is automatically generated using the IDENTITY strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /**
   * The name of the attribute.
   * This field is required and cannot be null.
   */
  @Column(nullable = false)
  private String attributeName;

  /**
   * The data type of the attribute (e.g., String, Integer, Boolean).
   * This field is required and cannot be null.
   */
  @Column(nullable = false)
  private String dataType;

  /**
   * The category to which this attribute belongs.
   * This relationship is managed using a many-to-one mapping.
   * The category_id column is a foreign key and cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;
}
