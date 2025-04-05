package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.MessageMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.MessageRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.util.*;

/**
 * Service class for handling messaging operations between users.
 * <p>
 * Provides functionalities for sending messages, retrieving conversations,
 * and fetching the latest messages in user chats.
 * </p>
 */
@Service
@AllArgsConstructor
public class MessageService {

  private static final Logger logger = LogManager.getLogger(MessageService.class);

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final MessageMapper messageMapper;

  /**
   * Sends a message from one user to another.
   *
   * @param messageRequest The {@link MessageRequest} containing message details.
   * @return A {@link MessageResponse} DTO representing the sent message.
   * @throws AppEntityNotFoundException if either the sender or receiver is not found.
   * @throws AccessDeniedException if the authenticated user does not match the sender.
   */
  public MessageResponse sendMessage(MessageRequest messageRequest) {
    UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User sender = userRepository.findById(messageRequest.getFromUserId())
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USERNAME_NOT_FOUND));

    if (!currentUser.getUsername().equals(sender.getUsername())) {
      logger.warn("Access denied: Current user '{}' does not match sender '{}'", currentUser.getUsername(), sender.getUsername());
      throw new AccessDeniedException("User in security context (" + currentUser.getUsername() + ") doesn't match sender in message request (" + sender.getUsername() + ")");
    }

    User receiver = userRepository.findById(messageRequest.getToUserId())
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USERNAME_NOT_FOUND));

    Message message = new Message()
            .setMessageText(messageRequest.getMessageText())
            .setFromUser(sender)
            .setToUser(receiver);

    messageRepository.save(message);
    logger.info("Message sent from user {} to user {}", sender.getId(), receiver.getId());

    return messageMapper.toDto(message);
  }

  /**
   * Retrieves all messages exchanged between two users.
   * <p>
   * Marks unread messages as read if they are directed to the current user.
   * </p>
   *
   * @param userId1 The ID of the first user.
   * @param userId2 The ID of the second user.
   * @return A list of {@link MessageResponse} objects.
   * @throws AppEntityNotFoundException if the current user is not found.
   * @throws AccessDeniedException if the current user is not one of the participants.
   */
  @Transactional
  public List<MessageResponse> getAllMessagesBetween(long userId1, long userId2) {
    // TODO: Paginate response for efficiency
    User currentUser = userRepository.findByUsername(getAuthenticatedUsername())
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USERNAME_NOT_FOUND));

    if (!Objects.equals(currentUser.getId(), userId1) && !Objects.equals(currentUser.getId(), userId2)) {
      logger.warn("Access denied: Current user {} is not part of conversation {} <-> {}", currentUser.getId(), userId1, userId2);
      throw new AccessDeniedException("Neither of the given userIds (" + userId1 + ", " + userId2 + ") match the current user (" + currentUser.getId() + ")");
    }

    User user1 = userService.getUserById(userId1);
    User user2 = userService.getUserById(userId2);

    List<Message> messages = getAllRawMessagesBetween(user1, user2);

    messages.stream()
            .filter(m -> !m.isRead() && m.getToUser().equals(currentUser))
            .forEach(m -> m.setRead(true));

    List<MessageResponse> responses = messages.stream()
            .map(messageMapper::toDto)
            .sorted(Comparator.comparing(MessageResponse::getSentAt).reversed())
            .toList();

    logger.info("Retrieved {} messages between user {} and user {}", responses.size(), userId1, userId2);

    return responses;
  }

  /**
   * Retrieves the newest message in each conversation for a given user.
   *
   * @param userID The ID of the user.
   * @return A list of {@link MessageResponse} objects representing the newest messages.
   * @throws AccessDeniedException if the user ID does not match the authenticated user.
   */
  public List<MessageResponse> getNewestMessages(long userID) {
    User currentUser = userService.getUserById(userID);

    if (!currentUser.getId().equals(userID)) {
      logger.warn("Access denied: Requested userId {} does not match current user {}", userID, currentUser.getId());
      throw new AccessDeniedException("Requested userId (" + userID + ") does not match current user (" + currentUser.getId() + ")");
    }

    List<Message> allMessages = new ArrayList<>();
    allMessages.addAll(messageRepository.findMessagesByToUser(currentUser));
    allMessages.addAll(messageRepository.findMessagesByFromUser(currentUser));

    Set<Long> userIdsCommunicatedWith = new HashSet<>();
    for (Message message : allMessages) {
      userIdsCommunicatedWith.add(message.getFromUser().getId());
      userIdsCommunicatedWith.add(message.getToUser().getId());
    }
    userIdsCommunicatedWith.remove(userID); // Remove current user ID

    List<MessageResponse> newestMessages = new ArrayList<>();

    for (Long otherUserId : userIdsCommunicatedWith) {
      User otherUser = userService.getUserById(otherUserId);
      List<Message> conversation = getAllRawMessagesBetween(currentUser, otherUser);

      if (!conversation.isEmpty()) {
        Message latestMessage = conversation.get(conversation.size() - 1);
        newestMessages.add(messageMapper.toDto(latestMessage));
      }
    }

    newestMessages.sort(Comparator.comparing(MessageResponse::getSentAt).reversed());

    logger.info("Retrieved {} latest messages for user {}", newestMessages.size(), userID);

    return newestMessages;
  }

  /**
   * Retrieves all raw message entities between two users.
   *
   * @param user1 The first user.
   * @param user2 The second user.
   * @return A list of {@link Message} entities, sorted by send time.
   */
  private List<Message> getAllRawMessagesBetween(User user1, User user2) {
    List<Message> messages = new ArrayList<>();
    messages.addAll(messageRepository.findMessagesByFromUserAndToUser(user1, user2));
    messages.addAll(messageRepository.findMessagesByFromUserAndToUser(user2, user1));

    messages.sort(Comparator.comparing(Message::getSentAt));
    return messages;
  }

  /**
   * Retrieves the username of the currently authenticated user.
   *
   * @return the username as a {@link String}.
   */
  private String getAuthenticatedUsername() {
    return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
  }
}
