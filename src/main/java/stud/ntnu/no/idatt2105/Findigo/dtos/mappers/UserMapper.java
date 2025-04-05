package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

/**
 * Mapper class responsible for converting {@link User} entities into {@link UserResponse} and {@link UserLiteResponse} DTOs,
 * and vice versa.
 * <p>
 * This class also handles mapping from {@link UserRequest} to {@link User} entity,
 * including password encoding.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

  private static final Logger logger = LogManager.getLogger(UserMapper.class);

  private final PasswordEncoder passwordEncoder;
  private final ListingMapper listingMapper;

  /**
   * Converts a {@link User} entity to a full {@link UserResponse} DTO.
   *
   * @param user the {@link User} entity to convert
   * @return a {@link UserResponse} containing the user's details
   */
  public UserResponse toDTO(User user) {
    logger.debug("Mapping User entity with id {} to UserResponse DTO", user.getId());

    return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getPhoneNumber(),
            user.getListings().stream()
                    .map(listingMapper::toDto)
                    .toList()
    );
  }

  /**
   * Converts a {@link User} entity to a lightweight {@link UserLiteResponse} DTO.
   *
   * @param user the {@link User} entity to convert
   * @return a {@link UserLiteResponse} containing basic user details
   */
  public UserLiteResponse toLiteDto(User user) {
    logger.debug("Mapping User entity with id {} to UserLiteResponse DTO", user.getId());

    return new UserLiteResponse(
            user.getId(),
            user.getUsername(),
            user.getPhoneNumber()
    );
  }

  /**
   * Converts a {@link UserRequest} DTO to a {@link User} entity.
   * Encodes the password before setting it to the entity.
   *
   * @param request the {@link UserRequest} containing user registration or update data
   * @return a {@link User} entity
   */
  public User toEntity(UserRequest request) {
    logger.debug("Mapping UserRequest to User entity for username '{}'", request.getUsername());

    return new User()
            .setUsername(request.getUsername())
            .setPassword(passwordEncoder.encode(request.getPassword()))
            .setPhoneNumber(request.getPhoneNumber())
            .setRoles(request.getRoles());
  }
}
