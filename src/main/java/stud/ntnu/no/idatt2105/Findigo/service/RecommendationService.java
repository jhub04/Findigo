package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.entities.BrowseHistory;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.repository.BrowseHistoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
  private final BrowseHistoryRepository browseHistoryRepository;
  private final ListingRepository listingRepository;

  /**
   * Gets recommended listings for a user based on their browsing history.
   * This method retrieves the most viewed categories from the user's recent browsing history
   * and recommends listings from those categories that the user has not already viewed.
   *
   * @param user The user for whom to get recommendations.
   * @param page The page number to retrieve.
   * @param size The number of listings per page.
   * @return A paginated list of recommended listings.
   */
  public Page<Listing> getRecommendedListingsForUser(User user, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
    Date cutoff = java.sql.Date.valueOf(tenDaysAgo);

    List<BrowseHistory> recentUserBrowseHistory = browseHistoryRepository.findByUserAndCreatedAtAfter(user, cutoff);

    // Most viewed categories
    Map<Category, Long> categoryFrequency = new HashMap<>();
    for (BrowseHistory browseHistory : recentUserBrowseHistory) {
      Category category = browseHistory.getListing().getCategory();
      categoryFrequency.put(category, categoryFrequency.getOrDefault(category, 0L) + 1);
    }

    List<Category> sortedCategories = categoryFrequency.entrySet().stream()
        .sorted(Map.Entry.<Category, Long>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .toList();


    Set<Long> alreadyViewedListingIds = recentUserBrowseHistory.stream()
        .map(b -> b.getListing().getId())
        .collect(Collectors.toSet()); //To exclude the listings a user already has viewed

    List<Listing> recommendedListings = new ArrayList<>();

    for (Category category : sortedCategories) {
      //Page of listings from a category
      Page<Listing> pageOfListings = listingRepository.findByCategoryAndIdNotIn(category, alreadyViewedListingIds, pageable);
      recommendedListings.addAll(pageOfListings.getContent());

      if (recommendedListings.size() >= size) {
        break;
      }
    }

    // Return a paginated result containing the recommended listings
    return new PageImpl<>(recommendedListings, pageable, recommendedListings.size());
  }

}
