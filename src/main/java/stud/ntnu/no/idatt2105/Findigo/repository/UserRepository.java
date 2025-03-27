package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.Optional;

/**
 * Repository interface for accessing {@link User} entities from the database.
 * Extends {@link JpaRepository} to provide CRUD operations.
 */
@Repository
public interface UserRepository
    extends JpaRepository<User, Long> {

  /**
   * Finds a user by their username.
   *
   * @param username the username of the user to find.
   * @return an {@link Optional} containing the user if found, otherwise empty.
   */
  Optional<User> findByUsername(String username);
}
