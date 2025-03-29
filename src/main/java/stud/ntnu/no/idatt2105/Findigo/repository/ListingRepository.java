package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
  //TODO javadoc
  List<Listing> findListingByUser(User user);
  List<Listing> findListingsByCategoryId(Long id);
}
