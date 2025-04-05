package stud.ntnu.no.idatt2105.Findigo.dtos.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for responding with message details.
 * <p>
 * Represents a message that has been sent and received between users.
 * </p>
 */
@Data
@AllArgsConstructor
@Schema(description = "DTO for responding with message details, including sender, recipient, content, and metadata.")
public class MessageResponse {

  /**
   * The ID of the user who sent the message.
   */
  @Schema(description = "The ID of the user who sent the message", example = "1")
  private long fromUserId;

  /**
   * The username of the sender.
   */
  @Schema(description = "The username of the sender", example = "sender_username")
  private String fromUsername;

  /**
   * The ID of the user who received the message.
   */
  @Schema(description = "The ID of the user who received the message", example = "2")
  private long toUserId;

  /**
   * The username of the recipient.
   */
  @Schema(description = "The username of the recipient", example = "recipient_username")
  private String toUsername;

  /**
   * The content of the message.
   */
  @Schema(description = "The content of the message", example = "Hello, I am interested in your listing!")
  private String messageText;

  /**
   * The unique identifier of the message.
   */
  @Schema(description = "The unique identifier of the message", example = "42")
  private long messageId;

  /**
   * Indicates whether the message has been read.
   */
  @Schema(description = "Indicates whether the message has been read", example = "false")
  private boolean isRead;

  /**
   * The timestamp of when the message was sent.
   */
  @Schema(description = "The timestamp of when the message was sent", example = "2025-04-05T14:30:00Z")
  private Date sentAt;
}
