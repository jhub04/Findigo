package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.*;

import java.util.ArrayList;
import java.util.List;

public class ListingMapper {

  public static Listing toEntity(ListingRequest request, User user, Category category, List<Attribute> attributeDefinitions) {
    Listing listing = new Listing()
            .setBriefDescription(request.getBriefDescription())
            .setFullDescription(request.getFullDescription())
            .setLatitude(request.getLatitude())
            .setLongitude(request.getLongitude())
            .setImageUrls(request.getImageUrls() != null ? request.getImageUrls() : new ArrayList<>())
            .setCategory(category)
            .setUser(user);

    List<ListingAttribute> listingAttributes = request.getAttributes().stream()
            .map(attrReq -> {
              Attribute attrDef = attributeDefinitions.stream()
                      .filter(a -> a.getId() == attrReq.getAttributeId())
                      .findFirst()
                      .orElseThrow(() -> new RuntimeException("Attribute definition not found: " + attrReq.getAttributeId()));

              return ListingAttributeMapper.toEntity(attrDef, attrReq.getValue(), listing);
            })
            .toList();

    listing.setListingAttributes(listingAttributes);
    return listing;
  }

  public static ListingResponse toDto(Listing listing) {
    List<ListingAttributeResponse> attributeResponses = listing.getListingAttributes().stream()
            .map(ListingAttributeMapper::toDto)
            .toList();

    return new ListingResponse(
            listing.getId(),
            listing.getBriefDescription(),
            listing.getFullDescription(),
            listing.getLatitude(),
            listing.getLongitude(),
            new ArrayList<>(listing.getImageUrls()),
            CategoryMapper.toDto(listing.getCategory()),
            new UserResponse(listing.getUser().getId(), listing.getUser().getUsername()),
            attributeResponses
    );
  }
}

