package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;

public class MessageMapper {

  public static MessageResponse toDto(Message message) {
    return new MessageResponse(
        message.getFromUser().getId(), message.getToUser().getId(), message.getMessageText(),
        message.getId(), message.isRead(), message.getSentAt());
  }
}
