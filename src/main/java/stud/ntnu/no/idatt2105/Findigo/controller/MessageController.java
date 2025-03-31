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
 * Provides endpoints for sending and retrieving messages between users.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Controller", description = "Handles message-related operations between users.")
public class MessageController {
  private final MessageService messageService;
  private static final Logger logger = LogManager.getLogger(MessageController.class);

  /**
   * Sends a new message from one user to another.
   *
   * @param messageRequest The request containing message details.
   * @return The response containing the sent message details.
   */
  @PostMapping("/send")
  @Operation(summary = "Send a message", description = "Sends a new message from one user to another.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully sent message"),
      @ApiResponse(responseCode = "403", description = "Unauthorized - User wanting to send message is not the logged in user"),
      @ApiResponse(responseCode = "404", description = "There is no user with the fromUser id")
  })
  public ResponseEntity<MessageResponse> sendNewMessage(@RequestBody MessageRequest messageRequest) {
    logger.info("Sending new message from userId {} to userId {}", messageRequest.getFromUserId(), messageRequest.getToUserId());
    MessageResponse messageResponse = messageService.sendMessage(messageRequest);
    logger.info("Message sent successfully from userId {} to userId {}", messageRequest.getFromUserId(), messageRequest.getToUserId());
    return ResponseEntity.ok(messageResponse);
  }

  /**
   * Retrieves all messages exchanged between two users.
   *
   * @param userId1 The ID of the first user.
   * @param userId2 The ID of the second user.
   * @return A list of messages exchanged between the users.
   */
  @GetMapping("/{userId1}/{userId2}")
  @Operation(summary = "Get messages between two users", description = "Fetches all messages exchanged between two users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all messages between the given users"),
      @ApiResponse(responseCode = "403", description = "Unauthorized - logged in user wanting to fetch messages does not match any of the given user ids"),
      @ApiResponse(responseCode = "404", description = "There is no user logged in")
  })
  public ResponseEntity<List<MessageResponse>> getAllMessagesBetweenUsers(
      @Parameter(description = "ID of the first user") @PathVariable long userId1,
      @Parameter(description = "ID of the second user") @PathVariable long userId2) {
    logger.info("Getting all messages between userId {} and userId {}", userId1, userId2);
    List<MessageResponse> messageResponses = messageService.getAllMessagesBetween(userId1, userId2);
    logger.info("Successfully fetched all messages between userId {} and userId {}", userId1, userId2);
    return ResponseEntity.ok(messageResponses);
  }

  /**
   * Retrieves the newest messages for a given user.
   *
   * @param userId The ID of the user.
   * @return A list of the most recent messages sent to or from the user.
   */
  @GetMapping("/{userId}")
  @Operation(summary = "Get newest messages for user", description = "Fetches the most recent messages sent to or from the user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all newest messages to or from the given users"),
      @ApiResponse(responseCode = "403", description = "Unauthorized - logged in user wanting to fetch messages does not match the given user id"),
      @ApiResponse(responseCode = "404", description = "There is no user with the given user id in the database")
  })
  public ResponseEntity<List<MessageResponse>> getNewestMessagesForUser(
      @Parameter(description = "ID of the user") @PathVariable long userId) {
    logger.info("Finding all newest messages to and from userId {}", userId);
    List<MessageResponse> newestMessagesToOrFromUser = messageService.getNewestMessages(userId);
    logger.info("Found all newest messages to or from userID {}", userId);
    return ResponseEntity.ok(newestMessagesToOrFromUser);
  }
}
