package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

@Component
public class MessageMapper {
  @Autowired
  private UserService userService;


  public MessageResponse toDto(Message message) {
    String fromUsername = userService.getUserById(message.getFromUser().getId()).getUsername();
    String toUsername = userService.getUserById(message.getToUser().getId()).getUsername();

    return new MessageResponse(
        message.getFromUser().getId(), fromUsername, message.getToUser().getId(), toUsername, message.getMessageText(),
        message.getId(), message.isRead(), message.getSentAt());
  }
}
