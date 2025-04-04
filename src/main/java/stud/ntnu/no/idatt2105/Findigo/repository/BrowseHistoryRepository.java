package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.BrowseHistory;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.Date;
import java.util.List;

@Repository
public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Long> {
  List<BrowseHistory> findByUserAndCreatedAtAfter(User user, Date cutoffDate);
}
