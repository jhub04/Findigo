package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * <p>
 * Provides CRUD operations and custom queries for user entities.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Checks if a user exists with the given username.
   *
   * @param username the username to check for existence
   * @return {@code true} if a user with the given username exists, otherwise {@code false}
   */
  boolean existsByUsername(String username);

  /**
   * Finds a user by their username.
   *
   * @param username the username of the user to find
   * @return an {@link Optional} containing the user if found, otherwise empty
   */
  Optional<User> findByUsername(String username);
}
