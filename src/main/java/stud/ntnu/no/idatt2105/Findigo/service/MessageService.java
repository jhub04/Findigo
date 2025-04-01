package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.controller.MessageController;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.MessageMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.repository.MessageRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;
import java.util.*;

/**
 * Service class for handling messages between users.
 * Implements methods for sending and retrieving messages.
 */
@Service
@AllArgsConstructor
public class MessageService {
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final MessageMapper messageMapper;
  private static final Logger logger = LogManager.getLogger(MessageController.class);


  /**
   * Sends a message from one user to another.
   *
   * @param messageRequest The {@link MessageRequest} containing the message details.
   * @return The saved {@link Message} entity.
   */
  public MessageResponse sendMessage(MessageRequest messageRequest){
    UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String fromUsername = userService.getUserById(messageRequest.getFromUserId()).getUsername();

    if (!currentUser.getUsername().equals(fromUsername)) {
      throw new AccessDeniedException("User in security context (" + currentUser.getUsername() + ") doesnt match with from user in the message request (" + fromUsername + ")");
    }

    Message message = new Message()
        .setMessageText(messageRequest.getMessageText())
        .setToUser(userRepository.findById(messageRequest.getToUserId())
            .orElseThrow( () -> new NoSuchElementException("No user with id " + messageRequest.getToUserId())))
        .setFromUser(userRepository.findById(messageRequest.getFromUserId())
            .orElseThrow( () -> new NoSuchElementException("No user with id " + messageRequest.getFromUserId()))
        );

    messageRepository.save(message);

    return messageMapper.toDto(message);
  }


  /**
   * Retrieves all messages between two users.
   *
   * @param userId1 The ID of the first user.
   * @param userId2 The ID of the second user.
   * @return A list of {@link MessageResponse} objects representing all messages between the two users.
   */
  @Transactional
  public List<MessageResponse> getAllMessagesBetween(long userId1, long userId2) {
    //TODO paginate response
    User currentUser = userService.getCurrentUser();
    if (!(currentUser.getId().equals(userId1) || currentUser.getId().equals(userId2))) {
      throw new AccessDeniedException("Neither of the given userIds (" + userId1 + ", " + userId2 +") match with userId of current user in the security context(" + currentUser.getId()+")");
    }
    User user1 = userService.getUserById(userId1);
    User user2 = userService.getUserById(userId2);

    List<Message> messages = messageRepository.findMessagesByFromUserAndToUser(user1, user2);
    messages.addAll(messageRepository.findMessagesByFromUserAndToUser(user2, user1));

    messages.stream()
        .filter(m -> !m.isRead() && m.getToUser().equals(currentUser))
        .forEach(m -> m.setRead(true));

    List<MessageResponse> messageResponses = new ArrayList<>(messages.stream().map(messageMapper::toDto).toList());
    messageResponses.sort(Comparator.comparing(MessageResponse::getSentAt));
    return messageResponses.reversed();
  }


  public List<MessageResponse> getNewestMessages(long userID) {
    User currentUser = userService.getUserById(userID);
    if (!currentUser.getId().equals(userID)) {
      throw new AccessDeniedException("UserId of requested messages(" + userID + ") does not match user Id of current user(" + currentUser.getId() + ")");
    }
    //Current user must here be user with userID given by param
    List<Message> allMessagesToOrFromUser = messageRepository.findMessagesByToUser(currentUser);
    allMessagesToOrFromUser.addAll(messageRepository.findMessagesByFromUser(currentUser)); //All messages that have been sent to or sent by the calling user

    Set<Long> userIdsCommunicatedWith = new HashSet<>();
    for (Message message: allMessagesToOrFromUser) {
      userIdsCommunicatedWith.add(message.getFromUser().getId());
      userIdsCommunicatedWith.add(message.getToUser().getId());
    }
    userIdsCommunicatedWith.remove(userID);
    List<MessageResponse> newestMessages= new ArrayList<>();

    for (Long otherUserId : userIdsCommunicatedWith) {
      User otherUser = userService.getUserById(otherUserId);
      List<Message> conversation = getAllRawMessagesBetween(currentUser, otherUser);

      if (!conversation.isEmpty()) {
        Message latest = conversation.get(conversation.size() - 1); // newest
        newestMessages.add(messageMapper.toDto(latest));
      }
    }

    newestMessages.sort(Comparator.comparing(MessageResponse::getSentAt));

    return newestMessages.reversed();

  }

  private List<Message> getAllRawMessagesBetween(User user1, User user2) {
    List<Message> messages = new ArrayList<>();
    messages.addAll(messageRepository.findMessagesByFromUserAndToUser(user1, user2));
    messages.addAll(messageRepository.findMessagesByFromUserAndToUser(user2, user1));
    messages.sort(Comparator.comparing(Message::getSentAt));
    return messages;
  }
}
