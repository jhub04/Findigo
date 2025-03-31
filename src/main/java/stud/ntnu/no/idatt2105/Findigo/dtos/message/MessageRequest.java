package stud.ntnu.no.idatt2105.Findigo.dtos.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for sending a message request.
 * This class represents a message sent from one user to another.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

  /**
   * The ID of the user sending the message.
   */
  private long fromUserId;

  /**
   * The ID of the user receiving the message.
   */
  private long toUserId;

  /**
   * The content of the message.
   */
  private String messageText;
}