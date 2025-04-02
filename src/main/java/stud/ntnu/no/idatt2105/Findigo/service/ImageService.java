package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.idatt2105.Findigo.controller.ListingController;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.ImageDownloadException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.ImageUploadException;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ImageService {
  private final String picturesPath = "src/main/resources/pictures/listing";
  private final ListingRepository listingRepository;
  private final UserService userService;
  private static final Logger logger = LogManager.getLogger(ImageService.class);

  /**
   * Uploads an image to a listing.
   *
   * @param listingId The ID of the listing to upload the image to.
   * @param file      The image file to upload.
   * @throws ImageUploadException if the image could not be uploaded.
   */
  public void uploadImageToListing(long listingId, MultipartFile file) {
    User currentUser = userService.getCurrentUser();
    Listing listing = listingRepository.findById(listingId)
        .orElseThrow(() -> new NoSuchElementException("Listing not found with ID " + listingId)); //TODO make method that does this and replace

    if (!listing.getUser().getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("Current logged in user (" + currentUser.getId() + ") does not match user (" + listing.getUser().getId() + ") of listing with ID " + listingId);
    }

    try {
      Path directoryPath = Paths.get(picturesPath + listingId + "/");
      Files.createDirectories(directoryPath);

      Path targetPath = directoryPath.resolve(file.getOriginalFilename());
      Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

      listing.addImageUrl(picturesPath + listingId + "/" + file.getOriginalFilename());
      listingRepository.save(listing);
      logger.info(listing.getImageUrls());
    } catch (IOException e) {
      throw new ImageUploadException("Could not upload image to listing with ID " + listingId + ", url: " + e.getMessage());
    }
  }

  /**
   * Downloads an image from a listing.
   *
   * @param listingId The ID of the listing to download the images from.
   * @return A list of resources containing the images.
   */
  public Resource downloadImageFromListing(long listingId, int imageIndex) {
    {
      Listing listing = listingRepository.findById(listingId)
          .orElseThrow(() -> new NoSuchElementException("Listing not found with ID " + listingId)); //TODO make method that does this and replace

      List<Resource> images = new ArrayList<>();

      for (String imageUrl : listing.getImageUrls()) {
        Path path = Paths.get(imageUrl);
        try {
          Resource resource = new UrlResource(path.toUri());
          images.add(resource);
        } catch (MalformedURLException e) {
          throw new ImageDownloadException("Could not download image from listing with ID " + listingId + ", the URL " + imageUrl + " is malformed. Exception message: " + e.getMessage());
        }
      }

      return images.get(imageIndex);
    }
  }
}
