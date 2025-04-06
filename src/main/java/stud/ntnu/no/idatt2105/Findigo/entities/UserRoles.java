package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Represents a user's role in the system.
 */
@Getter
@Setter
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_roles")
public class UserRoles {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, name = "role")
  @Enumerated(EnumType.STRING)
  private Role role;


  /**
   * Checks if two UserRoles objects are equal based on their ID.
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserRoles userRoles = (UserRoles) o;
    return id != null && id.equals(userRoles.id);
  }

  /**
   * Generates a hash code for the UserRoles object.
   *
   * @return the hash code of the object
   */
  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
