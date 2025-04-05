package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
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
import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class RecommendationService {
  Logger logger = LogManager.getLogger(RecommendationService.class);

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
  public Page<ListingResponse> getRecommendedListings(int page, int size) {
    User user = securityUtil.getCurrentUser();

    LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
    Date cutoff = java.sql.Date.valueOf(tenDaysAgo);

    List<BrowseHistory> recentUserBrowseHistory = browseHistoryRepository.findByUserAndCreatedAtAfter(user, cutoff);
    logger.info("User " + user.getUsername() + " has " + recentUserBrowseHistory.size() + " browse history entries in the last 10 days.");

    // Most viewed categories
    Map<Category, Long> categoryFrequency = new HashMap<>();
    for (BrowseHistory browseHistory : recentUserBrowseHistory) {
      Category category = browseHistory.getListing().getCategory();
      categoryFrequency.put(category, categoryFrequency.getOrDefault(category, 0L) + 1);
    }
    logger.info("User " + user.getUsername() + " has viewed " + categoryFrequency.size() + " categories in the last 10 days.");

    List<Category> sortedCategories = categoryFrequency.entrySet().stream()
        .sorted(Map.Entry.<Category, Long>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .toList();



    Set<Long> excludedListingIds = listingRepository.findListingsByUser(user).stream()
        .map(Listing::getId)
        .collect(Collectors.toSet());

    List<Listing> allRecommendedListings = new ArrayList<>();

    for (Category category : sortedCategories) {
      List<Listing> listingsInCategory = listingRepository.findByCategoryAndIdNotIn(category, excludedListingIds);
      allRecommendedListings.addAll(listingsInCategory);
    }
    logger.info("User " + user.getUsername() + " has " + allRecommendedListings.size() + " recommended listings.");

    // Apply pagination manually
    int start = Math.min(page * size, allRecommendedListings.size());
    int end = Math.min(start + size, allRecommendedListings.size());
    if (start > allRecommendedListings.size() || start < 0 || end > allRecommendedListings.size() || end < 0) {
      throw new IllegalArgumentException("Invalid page or size parameters");
    }
    logger.info("User " + user.getUsername() + " requested page " + page + " with size " + size + ". Start: " + start + ", End: " + end);
    List<Listing> pagedListings = allRecommendedListings.subList(start, end);
    logger.info("User " + user.getUsername() + " has " + pagedListings.size() + " recommended listings on page " + page);

    List<ListingResponse> pagedListingResponses = pagedListings.stream()
        .map(ListingMapper::toDto)
        .toList();
    return new PageImpl<>(pagedListingResponses, PageRequest.of(page, size), allRecommendedListings.size());
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
