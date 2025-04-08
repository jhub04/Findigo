package stud.ntnu.no.idatt2105.Findigo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.entities.UserRoles;

/**
 * Repository for managing user roles in the system.
 */
@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
  void deleteByUser(User user);
}
