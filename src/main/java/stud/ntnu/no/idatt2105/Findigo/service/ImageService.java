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
import stud.ntnu.no.idatt2105.Findigo.entities.ListingImageUrls;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityOperationException;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingImageRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling image upload and download operations related to listings.
 */
@Service
@RequiredArgsConstructor
public class ImageService {

  private static final Logger logger = LogManager.getLogger(ImageService.class);

  /**
   * The base path where listing images are stored.
   */
  private final String picturesPath = "src/main/resources/pictures/listing";

  private final ListingRepository listingRepository;
  private final UserService userService;
  private final SecurityUtil securityUtil;
  private final ListingImageRepository listingImageRepository;

  /**
   * Uploads an image to a specific listing.
   *
   * @param listingId the ID of the listing to upload the image to
   * @param file the image file to upload
   * @return the number of images currently associated with the listing
   * @throws AccessDeniedException if the current user is not the owner of the listing
   * @throws EntityOperationException if an error occurs during file upload
   * @throws AppEntityNotFoundException if the listing does not exist
   */
  public int uploadImageToListing(long listingId, MultipartFile file) {
    User currentUser = userService.getCurrentUser();
    Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    if (!securityUtil.isListingOwner(listing)) {
      logger.warn("Access denied: User {} attempted to upload image to listing owned by user {}", currentUser.getId(), listing.getUser().getId());
      throw new AccessDeniedException("Current logged in user (" + currentUser.getId() + ") does not match user (" + listing.getUser().getId() + ") of listing with ID " + listingId);
    }

    try {
      Path directoryPath = Paths.get(picturesPath + listingId + "/");
      Files.createDirectories(directoryPath);

      Path targetPath = directoryPath.resolve(file.getOriginalFilename());
      Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

      String currentImagePath = picturesPath + listingId + "/" + file.getOriginalFilename();
      ListingImageUrls imageUrl = new ListingImageUrls()
              .setImageUrl(currentImagePath)
              .setListing(listing);
      listingImageRepository.save(imageUrl);

      logger.info("Image uploaded successfully for listing ID {}: {}", listingId, currentImagePath);

    } catch (IOException e) {
      logger.error("Failed to upload image for listing ID {}", listingId, e);
      throw new EntityOperationException(CustomErrorMessage.IMAGE_UPLOAD_FAILED);
    }

    return listingImageRepository.findByListingId(listingId).size();
  }

  /**
   * Downloads a specific image from a listing by its index.
   *
   * @param listingId the ID of the listing
   * @param imageIndex the index of the image to download
   * @return a {@link Resource} representing the image file
   * @throws IllegalArgumentException if the image index is invalid
   * @throws EntityOperationException if the image could not be loaded
   * @throws AppEntityNotFoundException if the listing does not exist
   */
  public Resource downloadImageFromListing(long listingId, int imageIndex) {
    Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    List<ListingImageUrls> listingImageUrls = new ArrayList<>(listingImageRepository.findByListingId(listingId));

    if (imageIndex < 0 || imageIndex >= listingImageUrls.size()) {
      logger.warn("Invalid image index {} for listing ID {}. Total images: {}", imageIndex, listingId, listingImageUrls.size());
      throw new IllegalArgumentException("Image index cannot be negative or greater than the amount of images (" + listingImageUrls.size() + "), imageIndex is " + imageIndex);
    }

    Path imagePath = Paths.get(listingImageUrls.get(imageIndex).getImageUrl());

    try {
      Resource resource = new UrlResource(imagePath.toUri());
      logger.info("Image successfully loaded for listing ID {}: {}", listingId, imagePath);
      return resource;
    } catch (MalformedURLException e) {
      logger.error("Failed to load image for listing ID {} at path {}", listingId, imagePath, e);
      throw new EntityOperationException(CustomErrorMessage.IMAGE_DOWNLOAD_FAILED);
    }
  }
}
