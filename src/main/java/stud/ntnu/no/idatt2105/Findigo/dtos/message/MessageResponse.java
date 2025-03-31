package stud.ntnu.no.idatt2105.Findigo.dtos.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for responding with message details.
 * This class represents a message that has been sent and received.
 */
@Data
@AllArgsConstructor
public class MessageResponse {

  /**
   * The ID of the user who sent the message.
   */
  private long fromUserId;

  /**
   * The username of the sender.
   */
  private String fromUsername;

  /**
   * The ID of the user who received the message.
   */
  private long toUserId;

  /**
   * The username of the recipient.
   */
  private String toUsername;

  /**
   * The content of the message.
   */
  private String messageText;

  /**
   * The unique identifier of the message.
   */
  private long messageId;

  /**
   * Indicates whether the message has been read.
   */
  private boolean isRead;

  /**
   * The timestamp of when the message was sent.
   */
  private Date sentAt;
}