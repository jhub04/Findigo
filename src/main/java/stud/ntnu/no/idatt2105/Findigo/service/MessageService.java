package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.MessageMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.repository.MessageRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class MessageService {
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final MessageMapper messageMapper;
  //TODO javadoc

  public MessageResponse sendMessage(MessageRequest messageRequest){
    UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String fromUsername = userRepository.findById(messageRequest.getFromUserId())
        .orElseThrow(() -> new NoSuchElementException("No user with id " + messageRequest.getFromUserId())).getUsername();

    if (!currentUser.getUsername().equals(fromUsername)) {
      throw new AccessDeniedException("User in security context (" + currentUser.getUsername() + ") doesnt match with from user in the message request (" + fromUsername + ")");
    }

    Message message = new Message()
        .setMessageText(messageRequest.getMessageText())
        .setToUser(userRepository.findById(messageRequest.getToUserId())
            .orElseThrow( () -> new NoSuchElementException("No user with id " + messageRequest.getToUserId())))
        .setFromUser(userRepository.findById(messageRequest.getFromUserId())
            .orElseThrow( () -> new NoSuchElementException("No user with id " + messageRequest.getFromUserId())));

    messageRepository.save(message);

    return messageMapper.toDto(message);
  }

  public List<MessageResponse> getAllMessagesBetween(long userId1, long userId2) {
    //TODO paginate response
    User currentUser = userRepository.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
        .orElseThrow(() -> new NoSuchElementException("Couldn't find user"));
    if (!(currentUser.getId().equals(userId1) || currentUser.getId().equals(userId2))) {
      throw new AccessDeniedException("Neither of the given userIds (" + userId1 + ", " + userId2 +") match with userId of current user in the security context(" + currentUser.getId()+")");
    }
    User user1 = userService.getUserById(userId1);
    User user2 = userService.getUserById(userId2);

    List<Message> messages = messageRepository.findMessagesByFromUserAndToUser(user1, user2);
    messages.addAll(messageRepository.findMessagesByFromUserAndToUser(user2, user1));
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

    for (Long otherUserId:userIdsCommunicatedWith) {
      List<MessageResponse> tempList = getAllMessagesBetween(userID, otherUserId);
      newestMessages.add(tempList.getLast());
    }

    newestMessages.sort(Comparator.comparing(MessageResponse::getSentAt));

    return newestMessages.reversed();

  }
}
