package stud.ntnu.no.idatt2105.Findigo.controller.admin;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.controller.ListingController;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
@Tag(name = "Listings: admin", description = "Get listings from database with admin permission")
public class AdminUserController {

  private static final Logger logger = LogManager.getLogger(ListingController.class);
  private final UserService userService;

  @GetMapping("/{userId}/listings")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<ListingResponse>> getUserListings(
          @Parameter(description = "The id of the user listings") @PathVariable long userId
  ) {
    logger.info("Admin: fetching listings for user with id {}", userId);

    return ResponseEntity.ok(userService.getUserListings(userId));
  }
}
