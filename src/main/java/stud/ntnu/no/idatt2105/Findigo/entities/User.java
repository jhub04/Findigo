package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Represents a user in the system.
 * Implements {@link UserDetails} for integration with Spring Security.
 */
@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @CreationTimestamp
  @Column(updatable = false, name = "created_at")
  private Date createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Date updatedAt;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  @ManyToMany
  @JoinTable(
      name = "favorite_listings",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "listing_id")
  )
  private Set<Listing> favoriteListings = new HashSet<>();

  /**
   * Returns the authorities granted to the user.
   * Converts {@link Role} enums to Spring Security's {@link GrantedAuthority}.
   *
   * @return a collection of granted authorities.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
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
}