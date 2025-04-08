package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.service.MessageService;

import java.util.List;

/**
 * Controller for handling message-related operations.
 *
 * <p>Provides endpoints for sending and retrieving messages between users.</p>
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Endpoints for sending and retrieving messages between users")
public class MessageController {

  private static final Logger logger = LogManager.getLogger(MessageController.class);

  private final MessageService messageService;

  /**
   * Sends a new message from one user to another.
   *
   * @param messageRequest the request containing message details
   * @return the response containing the sent message details
   */
  @PostMapping("/send")
  @Operation(summary = "Send a message", description = "Sends a new message from one user to another.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully sent message"),
          @ApiResponse(responseCode = "403", description = "Unauthorized - Sender is not the logged-in user"),
          @ApiResponse(responseCode = "404", description = "Sender user not found")
  })
  public ResponseEntity<?> sendNewMessage(@RequestBody MessageRequest messageRequest) {
    logger.info("Sending new message from userId {} to userId {}", messageRequest.getFromUserId(), messageRequest.getToUserId());
    MessageResponse messageResponse = messageService.sendMessage(messageRequest);
    logger.info("Message sent from userId {} to userId {}", messageRequest.getFromUserId(), messageRequest.getToUserId());
    return ResponseEntity.ok(messageResponse);
  }

  /**
   * Retrieves all messages exchanged between two users.
   *
   * @param userId1 the ID of the first user
   * @param userId2 the ID of the second user
   * @return a list of messages exchanged between the users
   */
  @GetMapping("/{userId1}/{userId2}")
  @Operation(summary = "Get messages between two users", description = "Fetches all messages exchanged between two users.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved messages between users"),
          @ApiResponse(responseCode = "403", description = "Unauthorized - Logged-in user does not match any of the given user IDs"),
          @ApiResponse(responseCode = "404", description = "No user found")
  })
  public ResponseEntity<List<MessageResponse>> getAllMessagesBetweenUsers(
          @Parameter(description = "ID of the first user", example = "1") @PathVariable long userId1,
          @Parameter(description = "ID of the second user", example = "2") @PathVariable long userId2) {
    logger.info("Fetching all messages between userId {} and userId {}", userId1, userId2);
    List<MessageResponse> messageResponses = messageService.getAllMessagesBetween(userId1, userId2);
    logger.info("Successfully fetched {} messages between userId {} and userId {}", messageResponses.size(), userId1, userId2);
    return ResponseEntity.ok(messageResponses);
  }

  /**
   * Retrieves the newest messages for a given user.
   *
   * @param userId the ID of the user
   * @return a list of the most recent messages sent to or from the user
   */
  @GetMapping("/{userId}")
  @Operation(summary = "Get newest messages for user", description = "Fetches the most recent messages sent to or from the user.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved newest messages for user"),
          @ApiResponse(responseCode = "403", description = "Unauthorized - Logged-in user does not match the given user ID"),
          @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<?> getNewestMessagesForUser(
          @Parameter(description = "ID of the user to retrieve messages for", example = "1") @PathVariable long userId) {
    logger.info("Fetching newest messages for userId {}", userId);
    List<MessageResponse> newestMessagesToOrFromUser = messageService.getNewestMessages(userId);
    logger.info("Fetched {} newest messages for userId {}", newestMessagesToOrFromUser.size(), userId);
    return ResponseEntity.ok(newestMessagesToOrFromUser);
  }
}
