package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityOperationException;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class ImageService {
  private final String picturesPath = "src/main/resources/pictures/listing";
  private final ListingRepository listingRepository;
  private final UserService userService;
  private static final Logger logger = LogManager.getLogger(ImageService.class);
  private final SecurityUtil securityUtil;

  /**
   * Uploads an image to a listing.
   *
   * @param listingId The ID of the listing to upload the image to.
   * @param file      The image file to upload.
   * @throws ImageUploadException if the image could not be uploaded.
   */
  public int uploadImageToListing(long listingId, MultipartFile file) {
    User currentUser = userService.getCurrentUser();
    Listing listing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND)); //TODO make method that does this and replace

    if (securityUtil.isListingOwner(listing)) {
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
      throw new EntityOperationException(CustomErrorMessage.IMAGE_UPLOAD_FAILED);
    }
    return listing.getImageUrls().size();
  }

  /**
   * Downloads an image from a listing.
   *
   * @param listingId The ID of the listing to download the images from.
   * @return A list of resources containing the images.
   */
  public Resource downloadImageFromListing(long listingId, int imageIndex) {
    Listing listing = listingRepository.findById(listingId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND)); //TODO make method that does this and replace

    if (imageIndex < 0 || imageIndex >= listing.getImageUrls().size()) {
      throw new IllegalArgumentException("Image index cannot be negative or greater than the amount of images (" +listing.getImageUrls().size() + "), imageIndex is " + imageIndex);
    }

    Resource resource;
    Path path = Paths.get(listing.getImageUrls().get(imageIndex));

    try {
      resource = new UrlResource(path.toUri());
    } catch (MalformedURLException e) {
      throw new EntityOperationException(CustomErrorMessage.IMAGE_DOWNLOAD_FAILED);
    }

    return resource;
  }
}
