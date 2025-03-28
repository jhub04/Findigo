package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {

  private final ListingRepository listingRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;


  public Listing addListing(String username, ListingRequest req) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Category category = categoryRepository.findById(req.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));

    Listing listing = ListingMapper.toEntity(
            req,
            user,
            category,
            category.getAttributes()
    );

    return listingRepository.save(listing);
  }

  public List<Listing> getUserListings(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return listingRepository.findListingByUser(user);
  }

}
