package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.BrowseHistory;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for managing {@link BrowseHistory} entities.
 * <p>
 * Provides CRUD operations and custom queries related to user browsing history.
 * </p>
 */
@Repository
public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Long> {

  /**
   * Retrieves all browse history entries for a given user that were created after the specified cutoff date.
   *
   * @param user the user whose browse history is to be retrieved
   * @param cutoffDate the date after which browse history entries should be retrieved
   * @return a list of {@link BrowseHistory} entries matching the criteria
   */
  List<BrowseHistory> findByUserAndCreatedAtAfter(User user, Date cutoffDate);
}
