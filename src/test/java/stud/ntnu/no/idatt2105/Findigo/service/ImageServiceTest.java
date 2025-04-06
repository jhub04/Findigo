package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class ImageServiceTest {
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
  private CategoryRepository categoryRepository;
  @Autowired
  private AttributeService attributeService;
  private long category1Id;
  @BeforeEach
  void setUp() {
    categoryRepository.deleteAll();

    CategoryRequest categoryRequest = new CategoryRequest("category1");
    category1Id = categoryService.createCategory(categoryRequest).getId();

    AttributeRequest attributeRequest = new AttributeRequest("att1", "string", category1Id);
    AttributeRequest attributeRequest2 = new AttributeRequest("att2", "string", category1Id);

    AttributeResponse attributeResponse1 = attributeService.createAttribute(attributeRequest);
    AttributeResponse attributeResponse2 = attributeService.createAttribute(attributeRequest2);

    ListingAttributeRequest listingAttributeRequest = new ListingAttributeRequest()
        .setAttributeId(attributeResponse1.getId())
        .setValue("value1");

    ListingRequest listingRequest = new ListingRequest()
        .setAddress("Test Address")
        .setBriefDescription("Test Description")
        .setFullDescription("Test Full Description")
        .setLatitude(63.4305)
        .setLongitude(10.3951)
        .setPrice(1500.00)
        .setCategoryId(1L)
        .setPostalCode("3012")
        .setAttributes(List.of(listingAttributeRequest));
    listingService.addListing(listingRequest);
    // Set up any necessary test data or configurations here
  }
}
