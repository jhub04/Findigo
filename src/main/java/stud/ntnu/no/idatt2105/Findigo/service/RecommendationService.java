package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
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

/**
 * Service for generating listing recommendations based on user browsing history.
 * <p>
 * Recommendations are based on the most frequently visited categories
 * by the current user in the last 10 days.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

  private final BrowseHistoryRepository browseHistoryRepository;
  private final ListingRepository listingRepository;
  private final SecurityUtil securityUtil;

  /**
   * Retrieves recommended listings for the current user.
   * <p>
   * Recommendations are based on categories most frequently browsed
   * by the user in the past 10 days. Listings the user has already viewed
   * are excluded from the recommendations.
   * </p>
   *
   * @param page the page number to retrieve (zero-based)
   * @param size the number of listings per page
   * @return a paginated {@link Page} of recommended listings
   */
  public Page<Listing> getRecommendedListings(int page, int size) {
    User currentUser = securityUtil.getCurrentUser();
    Pageable pageable = PageRequest.of(page, size);

    // Calculate cutoff date for recent history
    LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
    Date cutoffDate = Date.valueOf(tenDaysAgo);

    // Fetch recent browsing history
    List<BrowseHistory> recentHistory = browseHistoryRepository.findByUserAndCreatedAtAfter(currentUser, cutoffDate);

    // Count frequency of each category
    Map<Category, Long> categoryFrequency = recentHistory.stream()
            .collect(Collectors.groupingBy(
                    browse -> browse.getListing().getCategory(),
                    Collectors.counting()
            ));

    // Sort categories by descending frequency
    List<Category> sortedCategories = categoryFrequency.entrySet().stream()
            .sorted(Map.Entry.<Category, Long>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .toList();

    // Collect already viewed listing IDs to exclude them from recommendations
    Set<Long> viewedListingIds = recentHistory.stream()
            .map(browse -> browse.getListing().getId())
            .collect(Collectors.toSet());

    List<Listing> recommendedListings = new ArrayList<>();

    // Collect recommended listings from most browsed categories
    for (Category category : sortedCategories) {
      Page<Listing> listingsPage = listingRepository.findByCategoryAndIdNotIn(category, viewedListingIds, pageable);
      recommendedListings.addAll(listingsPage.getContent());

      if (recommendedListings.size() >= size) {
        break; // Stop if we've collected enough recommendations
      }
    }

    return new PageImpl<>(recommendedListings, pageable, recommendedListings.size());
  }

  /**
   * Adds a listing to the browsing history of the current user.
   * <p>
   * Useful for tracking which listings the user has viewed for future recommendations.
   * </p>
   *
   * @param listing the {@link Listing} entity to add to browsing history
   */
  public void addListingToBrowseHistory(Listing listing) {
    User currentUser = securityUtil.getCurrentUser();
    BrowseHistory browseHistory = new BrowseHistory()
            .setUser(currentUser)
            .setListing(listing);

    browseHistoryRepository.save(browseHistory);
  }
}
