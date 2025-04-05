package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.ListingImageUrls;

import java.util.List;

@Repository
public interface ListingImageRepository extends JpaRepository<ListingImageUrls, Long> {
  List<ListingImageUrls> findByListingId(Long listingId);
}
