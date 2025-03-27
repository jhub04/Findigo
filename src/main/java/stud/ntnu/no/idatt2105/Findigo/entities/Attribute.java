package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="Attribute")
public class Attribute {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String attributeName;

  @Column(nullable = false)
  private String dataType;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;
}
