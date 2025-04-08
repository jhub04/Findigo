package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageRequest;
import stud.ntnu.no.idatt2105.Findigo.entities.BrowseHistory;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.repository.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class RecommendationServiceTest {
  @Autowired
  private BrowseHistoryRepository browseHistoryRepository;
  @Autowired
  private RecommendationService recommendationService;
  @Autowired
  private AttributeService attributeService;
  @Autowired
  private ListingService listingService;
  @Autowired
  private ListingRepository listingRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CategoryService categoryService;
  private User user1;
  private User user2;
  private long category1Id;
  private ListingResponse listing;
  @Autowired
  private CategoryRepository categoryRepository;
  ListingAttributeRequest listingAttributeRequest;

  @BeforeEach
  public void setUp() {
    listingRepository.deleteAll();
    userRepository.deleteAll();
    categoryRepository.deleteAll();

    AuthRequest registerRequest1 = new AuthRequest();
    registerRequest1.setUsername("existingUser");
    registerRequest1.setPassword("password123");
    userService.register(registerRequest1);
    user1 = userService.getUserByUsername("existingUser");

    AuthRequest registerRequest2 = new AuthRequest().setUsername("user2").setPassword("password123");
    userService.register(registerRequest2);
    user2 = userService.getUserByUsername("user2");

    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));

    CategoryRequest categoryRequest = new CategoryRequest("category1");
    category1Id = categoryService.createCategory(categoryRequest).getId();

    AttributeRequest attributeRequest = new AttributeRequest("att1", "string", category1Id);
    AttributeRequest attributeRequest2 = new AttributeRequest("att2", "string", category1Id);

    AttributeResponse attributeResponse1 = attributeService.createAttribute(attributeRequest);
    AttributeResponse attributeResponse2 = attributeService.createAttribute(attributeRequest2);

    listingAttributeRequest = new ListingAttributeRequest()
        .setAttributeId(attributeResponse1.getId())
        .setValue("value1");
    ListingAttributeRequest listingAttributeRequest2 = new ListingAttributeRequest()
        .setAttributeId(attributeResponse2.getId())
        .setValue("value2");

    ListingRequest listingRequest = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(category1Id)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest, listingAttributeRequest2));
    listing = listingService.addListing(listingRequest); //Will be made by user 2
  }

  @Test
  public void testGetRecommendedListingsWithNoBrowsingHistory() {
    browseHistoryRepository.deleteAll();
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    List<ListingResponse> recommendedListings = recommendationService.getRecommendedListings(0, 10).getContent();
    assertEquals(1, recommendedListings.size());
    assertEquals(listing.getId(), recommendedListings.get(0).getId());
  }

  @Test
  public void testGetRecommendedListingsWithBrowseHistory() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    BrowseHistory browseHistory = new BrowseHistory()
        .setUser(user1)
        .setListing(listingRepository.findById(listing.getId()).orElseThrow(() -> new IllegalArgumentException("Listing not found")));
    browseHistoryRepository.save(browseHistory); //user1 views the listing made in setUp(owned by user2)
    List<ListingResponse> recommendedListings = recommendationService.getRecommendedListings(0, 10).getContent();//Get recommendations for user1
    assertEquals(1, recommendedListings.size());
    assertEquals(listing.getId(), recommendedListings.get(0).getId());
  }

  @Test
  public void testGetRecommendedListingsExcludesOwnListings() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    ListingRequest listingRequest = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(category1Id)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));
    ListingResponse listingResponse = listingService.addListing(listingRequest); //Will be made by user 1 and shouldn't be recommended for them
    List<ListingResponse> recommendedListings = recommendationService.getRecommendedListings(0, 10).getContent();
    assertEquals(1, recommendedListings.size());
    assertEquals(listing.getId(), recommendedListings.get(0).getId());
    listingRepository.deleteById(listingResponse.getId());
  }
  @Test
  public void testGetRecommendedListingsInvalidPageData() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    BrowseHistory browseHistory = new BrowseHistory()
        .setUser(user1)
        .setListing(listingRepository.findById(listing.getId()).orElseThrow(() -> new IllegalArgumentException("Listing not found")));
    browseHistoryRepository.save(browseHistory); //user1 views the listing made in setUp(owned by user2)
    assertThrows(IllegalArgumentException.class, () -> {
      recommendationService.getRecommendedListings(-1, 10);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      recommendationService.getRecommendedListings(0, -10);
    });
  }

  @Test
  public void testAddListingToBrowseHistory() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    browseHistoryRepository.deleteAll();
    BrowseHistory browseHistory = new BrowseHistory()
        .setUser(user1)
        .setListing(listingRepository.findById(listing.getId()).orElseThrow(() -> new IllegalArgumentException("Listing not found")));
    browseHistoryRepository.save(browseHistory); //user1 views the listing made in setUp(owned by user2)
    List<BrowseHistory> browseHistories = browseHistoryRepository.findAll();
    assertEquals(1, browseHistories.size());
    assertEquals(user1.getId(), browseHistories.get(0).getUser().getId());
    assertEquals(listing.getId(), browseHistories.get(0).getListing().getId());
  }
}
