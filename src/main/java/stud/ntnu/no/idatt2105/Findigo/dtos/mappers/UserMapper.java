package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.AdminUserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Role;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.entities.UserRoles;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRolesRepository;

/**
 * Mapper class responsible for converting {@link User} entities into {@link UserResponse} and {@link UserLiteResponse} DTOs,
 * and vice versa.
 * <p>
 * This class also handles mapping from {@link AdminUserRequest} to {@link User} entity,
 * including password encoding.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

  private static final Logger logger = LogManager.getLogger(UserMapper.class);

  private final PasswordEncoder passwordEncoder;
  private final ListingMapper listingMapper;
  private final UserRolesRepository userRolesRepository;
  private final UserRepository userRepository;

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
   * Converts a {@link AdminUserRequest} DTO to a {@link User} entity.
   * Encodes the password before setting it to the entity.
   *
   * @param request the {@link AdminUserRequest} containing user registration or update data
   * @return a {@link User} entity
   */
  public User toEntity(AdminUserRequest request) {
    logger.debug("Mapping UserRequest to User entity for username '{}'", request.getUsername());

    User newUser = new User()
            .setUsername(request.getUsername())
            .setPassword(passwordEncoder.encode(request.getPassword()))
            .setPhoneNumber(request.getPhoneNumber());
    userRepository.save(newUser);
    for (Role role : request.getRoles()) {
      UserRoles userRole = new UserRoles()
              .setUser(newUser)
              .setRole(role);
      userRolesRepository.save(userRole);
    }
    return newUser;
  }
}
