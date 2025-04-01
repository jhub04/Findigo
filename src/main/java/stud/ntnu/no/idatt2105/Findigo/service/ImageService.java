package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.ImageUploadException;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ImageService {
  private final String picturesPath = "src/main/resources/pictures/";
  private final ListingRepository listingRepository;
  /**
   * Uploads an image to a listing.
   * @param listingId The ID of the listing to upload the image to.
   * @param file The image file to upload.
   * @throws ImageUploadException if the image could not be uploaded.
   */
  public void uploadImageToListing(long listingId, MultipartFile file){
    try {
      Path path = Paths.get(picturesPath + listingId + "/" + file.getOriginalFilename());
      Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new ImageUploadException("Could not upload image to listing with ID " + listingId);
    }

    Listing listing = listingRepository.findById(listingId)
        .orElseThrow(() -> new NoSuchElementException("Listing not found with ID " + listingId));

    listing.addImageUrl(picturesPath + listingId + "/" + file.getOriginalFilename());
  }
}
