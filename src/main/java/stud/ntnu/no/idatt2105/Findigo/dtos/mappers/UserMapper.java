package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

/**
 * Mapper class responsible for converting {@link User} entities into {@link UserResponse} DTOs.
 * <p>
 * This class provides a simple method to map user entity data into a response DTO
 * that can be used in API responses.
 * </p>
 */

@RequiredArgsConstructor
@Component
public class UserMapper {

  private final PasswordEncoder passwordEncoder;

  /**
   * Converts a {@link User} entity to a {@link UserResponse} DTO.
   * <p>
   * This method extracts the necessary user details (ID and username)
   * and returns a DTO containing this information.
   * </p>
   *
   * @param user the {@link User} entity to convert
   * @return a {@link UserResponse} DTO containing the user's ID and username
   */
  public UserResponse toDTO(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getListings().stream().map(ListingMapper::toDto).toList());
  }

  public UserLiteResponse toLiteDto(User user) {
    return new UserLiteResponse(user.getId(), user.getUsername());
  }

  public User toEntity(UserRequest request) {
    return new User()
            .setUsername(request.getUsername())
            .setPassword(passwordEncoder.encode(request.getPassword()))
            .setRoles(request.getRoles());
  }
}
