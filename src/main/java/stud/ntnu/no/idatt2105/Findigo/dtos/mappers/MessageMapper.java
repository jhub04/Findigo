package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;

/**
 * Mapper class responsible for converting {@link Message} entities into {@link MessageResponse} DTOs.
 * <p>
 * This class extracts sender and receiver details directly from the {@link Message} entity
 * to build the response DTO.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class MessageMapper {

  private static final Logger logger = LogManager.getLogger(MessageMapper.class);

  /**
   * Converts a {@link Message} entity to a {@link MessageResponse} DTO.
   *
   * @param message the {@link Message} entity to convert
   * @return a {@link MessageResponse} DTO containing the message details
   */
  public MessageResponse toDto(Message message) {
    logger.debug("Mapping Message entity with id {} to MessageResponse DTO", message.getId());

    return new MessageResponse(
            message.getFromUser().getId(),
            message.getFromUser().getUsername(),
            message.getToUser().getId(),
            message.getToUser().getUsername(),
            message.getMessageText(),
            message.getId(),
            message.isRead(),
            message.getSentAt()
    );
  }
}
