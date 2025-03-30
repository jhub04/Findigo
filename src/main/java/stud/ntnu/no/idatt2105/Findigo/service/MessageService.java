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

    return MessageMapper.toDto(message);
  }

  public List<MessageResponse> getAllMessagesBetween(long userId1, long userId2) {
    //TODO paginate response
    User currentUser = userRepository.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
        .orElseThrow(() -> new NoSuchElementException("Couldn't find user"));
    if (currentUser.getId() != userId1 || currentUser.getId() != userId2) {
      throw new AccessDeniedException("Neither of the given userIds (" + userId1 + ", " + userId2 +") match with userId of current user in the security context(" + currentUser.getId()+")");
    }

    List<Message> messages = messageRepository.findMessagesByFromUserAndToUser(userId1, userId2);
    messages.addAll(messageRepository.findMessagesByFromUserAndToUser(userId2, userId1));
    List<MessageResponse> messageResponses = new ArrayList<>(messages.stream().map(MessageMapper::toDto).toList());
    messageResponses.sort(Comparator.comparing(MessageResponse::getSentAt));
    return messageResponses;
  }
}
