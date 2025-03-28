package stud.ntnu.no.idatt2105.Findigo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

  private final ListingService listingService;

  @PostMapping("/{username}")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Listing> addListing(
      @PathVariable String username,
      @Valid @RequestBody ListingRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(listingService.addListing(username, request));
  }

  @GetMapping("/{username}")
  public ResponseEntity<List<Listing>> getUserListings(
      @PathVariable String username
  ) {
    return ResponseEntity.ok(listingService.getUserListings(username));
  }

}
