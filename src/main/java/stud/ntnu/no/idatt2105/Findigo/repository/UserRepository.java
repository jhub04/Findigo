package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.model.User;

import java.util.Optional;

@Repository
public interface UserRepository
    extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
