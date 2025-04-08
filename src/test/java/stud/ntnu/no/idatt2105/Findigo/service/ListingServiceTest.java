package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.FilterListingsRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.sale.SaleResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.ListingStatus;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class ListingServiceTest {
  @Autowired
  private BrowseHistoryRepository browseHistoryRepository;
  @Autowired
  private SaleRepository saleRepository;
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
  private User user3;
  private User user4;
  private long category1Id;
  private ListingResponse listing;
  @Autowired
  private CategoryRepository categoryRepository;
  ListingAttributeRequest listingAttributeRequest;
  ListingResponse listing2;

  @BeforeEach
  public void setUp() {
    saleRepository.deleteAll();
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

    AuthRequest registerRequest3 = new AuthRequest().setUsername("user3").setPassword("password123");
    userService.register(registerRequest3);
    user3 = userService.getUserByUsername("user3");
    AuthRequest registerRequest4 = new AuthRequest().setUsername("user4").setPassword("password123");
    userService.register(registerRequest4);
    user4 = userService.getUserByUsername("user4");

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
        .setAttributes(new ArrayList<>(List.of(listingAttributeRequest, listingAttributeRequest2)));
    listing = listingService.addListing(listingRequest); //Will be made by user 2
  }

  @Test
  public void testAddListing() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));

    ListingRequest listingRequest2 = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(category1Id)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));
    listing2 = listingService.addListing(listingRequest2); //Will be made by user 1

    assertEquals(listing2.getAddress(), listingRequest2.getAddress());
    assertEquals(listing2.getBriefDescription(), listingRequest2.getBriefDescription());
    assertEquals(listing2.getFullDescription(), listingRequest2.getFullDescription());
    assertEquals(listing2.getLatitude(), listingRequest2.getLatitude());
    assertEquals(listing2.getLongitude(), listingRequest2.getLongitude());
    assertEquals(listing2.getPrice(), listingRequest2.getPrice());
    assertEquals(listing2.getListingStatus(), ListingStatus.ACTIVE);
    listingRepository.deleteById(listing2.getId());
  }

  @Test
  public void testAddListingInvalidCategory() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));

    ListingRequest listingRequest2 = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(99999L) // Invalid category ID
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));

    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.addListing(listingRequest2);
    });
  }
  @Test
  public void testGetListingsInCategory() {
    List<ListingResponse> listingsInCategory = listingService.getListingsInCategory(category1Id);
    assertEquals(1, listingsInCategory.size());
  }

  @Test
  public void testGetAllListings() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    List<ListingResponse> allListings = listingService.getAllListings();
    assertEquals(1, allListings.size());

    ListingRequest listingRequest2 = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(category1Id)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));
    listing2 = listingService.addListing(listingRequest2); //Will be made by user 1
    assertEquals(1, listingService.getAllListings().size());//Shouldn't get user1's own lisitngs
    listingRepository.deleteById(listing2.getId());
  }

  @Test
  public void testGetListingById() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    ListingResponse foundListing = listingService.getListingById(listing.getId());
    assertEquals(listing.getId(), foundListing.getId());
    assertEquals(listing.getAddress(), foundListing.getAddress());
    assertEquals(listing.getBriefDescription(), foundListing.getBriefDescription());
    assertEquals(listing.getFullDescription(), foundListing.getFullDescription());
    assertEquals(listing.getLatitude(), foundListing.getLatitude());
    assertEquals(listing.getLongitude(), foundListing.getLongitude());
    assertEquals(listing.getPrice(), foundListing.getPrice());
    assertEquals(listing.getCategory().getId(), foundListing.getCategory().getId());

    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.getListingById(99999L); // Invalid ID
    });

    //check that its aded to the browse history
    assertEquals(1, browseHistoryRepository.findByUser(user1).size());
  }

  @Test
  public void testEditMyListing() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.editMyListing(99999L, new ListingRequest()); // Invalid ID
    });
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    assertThrows(AccessDeniedException.class, () -> {
      listingService.editMyListing(listing.getId(), new ListingRequest()); // Not the owner
    });

    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));
    ListingRequest listingRequest = new ListingRequest()
        .setAddress("New Address")
        .setBriefDescription("New Description")
        .setFullDescription("New Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(2000.00)
        .setCategoryId(99999L)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.editMyListing(listing.getId(), listingRequest); // Invalid category ID
    });
    listingRequest.setCategoryId(category1Id);
    ListingResponse updatedListing = listingService.editMyListing(listing.getId(), listingRequest);
    assertEquals(listing.getId(), updatedListing.getId());
    assertEquals(listingRequest.getAddress(), updatedListing.getAddress());
    assertEquals(listingRequest.getBriefDescription(), updatedListing.getBriefDescription());
    assertEquals(listingRequest.getFullDescription(), updatedListing.getFullDescription());
  }

  @Test
  public void testEditListingAsAdmin() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.editMyListing(99999L, new ListingRequest()); // Invalid ID
    });
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user3, null, user3.getAuthorities()));
    ListingRequest listingRequest = new ListingRequest()
        .setAddress("New Address again")
        .setBriefDescription("New Description again")
        .setFullDescription("New Full Description again")
        .setLatitude(66.4305)
        .setLongitude(60.3951)
        .setPrice(200.00)
        .setCategoryId(9999L)
        .setPostalCode("3012")
        .setAttributes(new ArrayList<>(List.of(listingAttributeRequest)));
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.editListingAsAdmin(listing.getId(), listingRequest); // Invalid category ID
    });
    listingRequest.setCategoryId(category1Id);
    ListingResponse updatedListing = listingService.editListingAsAdmin(listing.getId(), listingRequest);
    assertEquals(listing.getId(), updatedListing.getId());
    assertEquals(listingRequest.getAddress(), updatedListing.getAddress());
    assertEquals(listingRequest.getBriefDescription(), updatedListing.getBriefDescription());
    assertEquals(listingRequest.getFullDescription(), updatedListing.getFullDescription());
  }

  @Test
  public void testDeleteListing() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.deleteListing(99999L); // Invalid ID
    });
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    assertThrows(AccessDeniedException.class, () -> {
      listingService.deleteListing(listing.getId()); // Not the owner
    });

    ListingRequest listingRequest2 = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(category1Id)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));
    listing2 = listingService.addListing(listingRequest2); //Will be made by user 1
    listingService.deleteListing(listing2.getId());
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.getListingById(listing2.getId()); // Invalid ID
    });
  }

  @Test
  public void testDeleteListingAsAdmin() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.deleteListingAsAdmin(9999999L); // Invalid ID
    });
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user3, null, user3.getAuthorities()));
    ListingRequest listingRequest2 = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(category1Id)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));
    listing2 = listingService.addListing(listingRequest2); //Will be made by user 3
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    listingService.deleteListingAsAdmin(listing2.getId());
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.getListingById(listing2.getId()); // Invalid ID
    });
  }

  @Test
  public void testGetFilteredListingInvalidPageData() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    assertThrows(IllegalArgumentException.class, () -> {
      listingService.getFilteredListings(-1, -1, new FilterListingsRequest()); // Invalid page data
    });
    assertThrows(IllegalArgumentException.class, () -> {
      listingService.getFilteredListings(-1, 1, new FilterListingsRequest()); // Invalid page data
    });
    assertThrows(IllegalArgumentException.class, () -> {
      listingService.getFilteredListings(10, -5, new FilterListingsRequest()); // Invalid page data
    });
  }

  @Test
  public void testGetAllFilteredListings() {
    //TODO make test for that method
  }

  @Test
  public void testMarkListingAsArchivedAndSold() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      listingService.markListingAsSold(99999L); // Invalid ID
    });
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    ListingRequest listingRequest2 = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(category1Id)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));
    listing2 = listingService.addListing(listingRequest2); //Will be made by user 1
    listingService.markListingAsArchived(listing2.getId());
    listing2 = listingService.getListingById(listing2.getId());
    assertEquals(ListingStatus.ARCHIVED, listing2.getListingStatus());

    assertThrows(IllegalStateException.class, () -> {
      listingService.markListingAsArchived(listing2.getId()); // Invalid ID
    });

    assertThrows(IllegalStateException.class, () -> {
      listingService.markListingAsSold(listing2.getId()); // Invalid ID
    });

    listingService.markListingAsActive(listing2.getId());
    listing2 = listingService.getListingById(listing2.getId());
    assertEquals(ListingStatus.ACTIVE, listing2.getListingStatus());
    listingService.markListingAsSold(listing2.getId());
    saleRepository.deleteAll();
    listing2 = listingService.getListingById(listing2.getId());
    assertEquals(ListingStatus.SOLD, listing2.getListingStatus());
    assertThrows(IllegalStateException.class, () -> {
      listingService.markListingAsSold(listing2.getId()); // Invalid ID
    });
    assertThrows(IllegalStateException.class, () -> {
      listingService.markListingAsArchived(listing2.getId()); // Invalid ID
    });

    listingService.deleteListing(listing2.getId());
  }

  @Test
  public void testArchiveAndSellListingInvalidUser() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    assertThrows(AccessDeniedException.class, () -> {
      listingService.markListingAsArchived(listing.getId()); // Not the owner
    });

    assertThrows(AccessDeniedException.class, () -> {
      listingService.markListingAsActive(listing.getId()); // Not the owner
    });
  }

}
