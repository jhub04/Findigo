package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityOperationException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingImageRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class ImageServiceTest {
  @Autowired
  private ListingImageRepository listingImageRepository;
  @Autowired
  private ImageService imageService;
  @Autowired
  private UserService userService;
  @Autowired
  private ListingService listingService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private ListingRepository listingRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private AttributeService attributeService;
  private long category1Id;
  private ListingResponse listing;
  private User user1;
  private User user2;
  @BeforeEach
  void setUp() {
    categoryRepository.deleteAll();
    userRepository.deleteAll();
    AuthRequest registerRequest1 = new AuthRequest();
    registerRequest1.setUsername("existingUser");
    registerRequest1.setPassword("password123");
    userService.register(registerRequest1);
    user1 = userService.getUserByUsername("existingUser");

    AuthRequest registerRequest2 = new AuthRequest().setUsername("user2").setPassword("password123");
    userService.register(registerRequest2);
    user2 = userService.getUserByUsername("user2");

    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));

    CategoryRequest categoryRequest = new CategoryRequest("category1");
    category1Id = categoryService.createCategory(categoryRequest).getId();

    AttributeRequest attributeRequest = new AttributeRequest("att1", "string", category1Id);
    AttributeRequest attributeRequest2 = new AttributeRequest("att2", "string", category1Id);

    AttributeResponse attributeResponse1 = attributeService.createAttribute(attributeRequest);
    AttributeResponse attributeResponse2 = attributeService.createAttribute(attributeRequest2);

    ListingAttributeRequest listingAttributeRequest = new ListingAttributeRequest()
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
    listing = listingService.addListing(listingRequest);
  }

  @Test
  public void testUploadImages() {
    MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
    MultipartFile file2 = new MockMultipartFile("file2", "test2.jpg", "image/jpeg", "test2".getBytes());
    imageService.uploadImageToListing(listing.getId(), file2);
    assertEquals(imageService.uploadImageToListing(listing.getId(), file), 2);
  }

  @Test
  public void testUploadImageFail() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      imageService.uploadImageToListing(9999L, new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes()));
    });
  }

  @Test
  public void testUploadImageAccessDenied() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));
    assertThrows(AccessDeniedException.class, () -> {
      imageService.uploadImageToListing(listing.getId(), new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes()));
    });
  }

  @Test
  public void testUploadNullImage() {
    assertThrows(IllegalArgumentException.class, () -> {
      imageService.uploadImageToListing(listing.getId(), null);
    });
  }

  @Test
  public void testUploadEmptyImage() {
    assertThrows(IllegalArgumentException.class, () -> {
      imageService.uploadImageToListing(listing.getId(), new MockMultipartFile("file", "", "image/jpeg","".getBytes()));
    });
  }

  @Test
  public void testUploadImageNoName () {
    assertThrows(IllegalArgumentException.class, () -> {
      imageService.uploadImageToListing(listing.getId(), new MockMultipartFile("", null, "image/jpeg","test".getBytes()));
    });
  }

  @Test
  public void testDownloadImageIllegalImageIndex() {
    assertThrows(IllegalArgumentException.class, () -> {
      imageService.downloadImageFromListing(listing.getId(), -1);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      imageService.downloadImageFromListing(listing.getId(), 99999999);
    });
  }

  @Test
  public void testDownloadImageListingNotFound() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      imageService.downloadImageFromListing(9999L, 0);
    });
  }

  @Test
  public void testUploadImageInvalidFilename() {
    MultipartFile file = new MockMultipartFile("file?/", "tes/?t.jpg", "image/jpeg", "test".getBytes());
    assertThrows(EntityOperationException.class, () -> {
      imageService.uploadImageToListing(listing.getId(), file);;
    });

  }

  @Test
  public void testDownloadImage() {
    MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
    imageService.uploadImageToListing(listing.getId(), file);
    assertEquals(imageService.downloadImageFromListing(listing.getId(), 0).getFilename(), "test.jpg");
  }

  @Test
  public void testDownloadImageInvalidListingId() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      imageService.downloadImageFromListing(9999L, 0);
    });
  }

  @Test
  public void testDeleteImage() {
    MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
    imageService.uploadImageToListing(listing.getId(), file);
    assertEquals(imageService.deleteImageFromListing(listing.getId(), 0), 1);
  }

  @Test
  public void testDeleteImageInvalidIndex() {
    assertThrows(IllegalArgumentException.class, () -> {
      imageService.deleteImageFromListing(listing.getId(), -1);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      imageService.deleteImageFromListing(listing.getId(), 99999999);
    });
  }

  @Test
  public void testDeleteImageListingNotFound() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      imageService.deleteImageFromListing(9999L, 0);
    });
  }

  @Test
  public void testDeleteImageAccessDenied() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user2.getAuthorities()));
    imageService.uploadImageToListing(listing.getId(), new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes()));
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));
    assertThrows(AccessDeniedException.class, () -> {
      imageService.deleteImageFromListing(listing.getId(), 0);
    });
  }

}
