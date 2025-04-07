package stud.ntnu.no.idatt2105.Findigo.dtos.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Data Transfer Object (DTO) for sending a message request.
 * <p>
 * Represents the structure of a message sent from one user to another.
 * </p>
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
@Schema(description = "DTO for sending a message from one user to another.")
public class MessageRequest {

  /**
   * The ID of the user sending the message.
   */
  @NotNull(message = "Sender user ID cannot be null")
  @Schema(description = "The ID of the user sending the message", example = "1")
  private long fromUserId;

  /**
   * The ID of the user receiving the message.
   */
  @NotNull(message = "Recipient user ID cannot be null")
  @Schema(description = "The ID of the user receiving the message", example = "2")
  private long toUserId;

  /**
   * The content of the message.
   */
  @NotBlank(message = "Message text cannot be blank")
  @Schema(description = "The content of the message", example = "Hello, are you interested in this item?")
  private String messageText;
}
