package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "listing_attribute")
public class ListingAttribute {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long Id;

  @Column(nullable = false)
  private String attributeValue;

  @ManyToOne
  @JoinColumn(name = "listing_id", nullable = false)
  private Listing listing;

  @ManyToOne
  @JoinColumn(name = "attribute_id", nullable = false)
  private Attribute attribute;
}
