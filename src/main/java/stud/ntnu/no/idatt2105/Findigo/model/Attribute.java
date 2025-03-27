package stud.ntnu.no.idatt2105.Findigo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.service.annotation.GetExchange;

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
