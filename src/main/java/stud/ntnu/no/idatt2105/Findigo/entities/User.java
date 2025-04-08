package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a user in the system.
 * Implements {@link UserDetails} for integration with Spring Security.
 */
@Getter
@Setter
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

  /**
   * Unique identifier for the user.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  /**
   * Username of the user.
   * Must be unique and not null.
   */
  @Column(unique = true, nullable = false)
  private String username;

  /**
   * Encrypted password of the user.
   */
  @Column(nullable = false)
  private String password;

  /**
   * Timestamp of when the user account was created.
   * Automatically set on creation.
   */
  @CreationTimestamp
  @Column(updatable = false, name = "created_at")
  private Date createdAt;

  /**
   * Timestamp of the last update to the user account.
   * Automatically updated on change.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private Date updatedAt;

  /**
   * Phone number of the user.
   */
  @Column
  private String phoneNumber;

  /**
   * List of listings created by the user.
   * Mapped by the user field in the Listing entity.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Listing> listings = new ArrayList<>();

  /**
   * List of browse history entries associated with the user.
   * Mapped by the user field in the BrowseHistory entity.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BrowseHistory> browseHistories = new ArrayList<>();

  /**
   * Favourites associated with this user.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FavoriteListings> favoriteListings = new ArrayList<>();

  /**
   * Set of roles assigned to the user.
   * Determines the user's access permissions.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<UserRoles> userRoles = new HashSet<>();

  /**
   * Returns the authorities granted to the user.
   * Converts {@link Role} enums to Spring Security's {@link GrantedAuthority}.
   *
   * @return a collection of granted authorities.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    System.out.println("Getting authorities for user: " + username);
    return userRoles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
            .toList();
  }

  /**
   * Indicates whether the user's account has expired.
   * Always returns {@code true}, meaning accounts do not expire.
   *
   * @return {@code true} since the account never expires.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user's account is locked.
   * Always returns {@code true}, meaning accounts are never locked.
   *
   * @return {@code true} since the account is never locked.
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials (password) have expired.
   * Always returns {@code true}, meaning credentials never expire.
   *
   * @return {@code true} since credentials never expire.
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled.
   * Always returns {@code true}, meaning all users are enabled.
   *
   * @return {@code true} since all users are enabled by default.
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * Provides a concise string representation of the user.
   *
   * @return string containing user ID and username.
   */
  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            '}';
  }

  /**
   * Compares this user with another object for equality.
   * Two users are considered equal if they have the same ID.
   *
   * @param o the object to compare with
   * @return {@code true} if the objects are equal, {@code false} otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return id != null && id.equals(user.id);
  }

  /**
   * Generates a hash code for the user.
   *
   * @return the hash code of the user
   */
  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
