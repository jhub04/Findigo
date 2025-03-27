package stud.ntnu.no.idatt2105.Findigo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "category")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String categoryName;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<Attribute> attributes = new ArrayList<>();

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<Listing> listings = new ArrayList<>();
}
