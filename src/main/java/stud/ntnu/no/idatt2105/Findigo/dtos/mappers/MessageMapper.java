package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

/**
 * Mapper class responsible for converting {@link Message} entities into {@link MessageResponse} DTOs.
 * <p>
 * This class uses {@link UserService} to fetch user details (such as usernames) based on the user IDs
 * contained in the {@link Message} entity.
 * </p>
 */
@Component
public class MessageMapper {

  @Autowired
  private UserService userService;

  /**
   * Converts a {@link Message} entity to a {@link MessageResponse} DTO.
   * <p>
   * This method extracts the sender and receiver's usernames using the {@link UserService}
   * and returns a {@link MessageResponse} that contains the message details along with user info.
   * </p>
   *
   * @param message the {@link Message} entity to convert
   * @return a {@link MessageResponse} DTO containing the message details
   */
  public MessageResponse toDto(Message message) {
    String fromUsername = userService.getUserById(message.getFromUser().getId()).getUsername();
    String toUsername = userService.getUserById(message.getToUser().getId()).getUsername();

    return new MessageResponse(
        message.getFromUser().getId(), fromUsername, message.getToUser().getId(), toUsername,
        message.getMessageText(), message.getId(), message.isRead(), message.getSentAt());
  }
}
