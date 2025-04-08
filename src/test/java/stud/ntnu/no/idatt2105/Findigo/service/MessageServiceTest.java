package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class MessageServiceTest {
  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private MessageService messageService;
  private User user1;
  private User user2;
  private User user3;
  private User user4;

  public MessageServiceTest() {
  }

  @BeforeEach
  public void setUp() {
    messageRepository.deleteAll();
    userRepository.deleteAll();

    AuthRequest registerRequest1 = new AuthRequest();
    registerRequest1.setUsername("existingUser");
    registerRequest1.setPassword("password123");
    userService.register(registerRequest1);
    user1 = userService.getUserByUsername("existingUser");

    AuthRequest registerRequest2 = new AuthRequest().setUsername("user2").setPassword("password123");
    userService.register(registerRequest2);
    user2 = userService.getUserByUsername("user2");

    AuthRequest registerRequest3 = new AuthRequest().setUsername("user3").setPassword("password123");
    userService.register(registerRequest3);
    user3 = userService.getUserByUsername("user3");
    AuthRequest registerRequest4 = new AuthRequest().setUsername("user4").setPassword("password123");
    userService.register(registerRequest4);
    user4 = userService.getUserByUsername("user4");

    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    MessageRequest messageRequest = new MessageRequest().setMessageText("Hello, this is a test message")
        .setFromUserId(user1.getId())
        .setToUserId(user2.getId());

    messageService.sendMessage(messageRequest);
  }

  @Test
  public void testGetMessagesBetween() {
    List<MessageResponse> messages = messageService.getAllMessagesBetween(user1.getId(), user2.getId());

    assertEquals("Hello, this is a test message", messages.get(0).getMessageText());
  }

  @Test
  public void testSendMessageWithIllegalSender() {
    MessageRequest messageRequest = new MessageRequest().setMessageText("Hello, this is a test message")
        .setFromUserId(user2.getId())
        .setToUserId(user1.getId());

    assertThrows(AccessDeniedException.class, () -> {
      messageService.sendMessage(messageRequest);
    });
  }

  @Test
  public void testSendMessageWithInvalidSenderId() {
    MessageRequest messageRequest = new MessageRequest().setMessageText("Hello, this is a test message")
        .setFromUserId(999L) // Invalid sender ID
        .setToUserId(user1.getId());

    assertThrows(AppEntityNotFoundException.class, () -> {
      messageService.sendMessage(messageRequest);
    });
  }

  @Test
  public void testGetMessagesBetweenWithInvalidUser() {
    assertThrows(AccessDeniedException.class, () -> {
      messageService.getAllMessagesBetween(999L, user2.getId()); // Invalid user ID
    });
  }

  @Test
  public void testGetMessagesBetweenWithAccessDenied() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));

    assertThrows(AccessDeniedException.class, () -> {
      messageService.getAllMessagesBetween(user1.getId(), 9999L);
    });
  }

  @Test
  public void testGetMessagesBetweenWithNoMessagesAndSendMessage() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));

    List<MessageResponse> messages = messageService.getAllMessagesBetween(user1.getId(), user3.getId());
    assertEquals(0, messages.size());
    MessageRequest messageRequest = new MessageRequest()
        .setMessageText("Hello, this is a test message")
        .setFromUserId(user1.getId())
        .setToUserId(user3.getId());

    MessageResponse messageResponse = messageService.sendMessage(messageRequest);

    assertEquals(messageRequest.getMessageText(), messageResponse.getMessageText());
    assertEquals(user1.getId(), messageResponse.getFromUserId());
    assertEquals(user3.getId(), messageResponse.getToUserId());
  }

  @Test
  public void testGetNewestMessagesBetween() {
    messageRepository.deleteAll();
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));

    MessageRequest messageRequest = new MessageRequest()
        .setMessageText("Hello, this is a test message")
        .setFromUserId(user1.getId())
        .setToUserId(user4.getId());

    MessageRequest messageRequest2 = new MessageRequest()
        .setMessageText("Hello, this is another test message")
        .setFromUserId(user1.getId())
        .setToUserId(user4.getId());

    messageService.sendMessage(messageRequest);
    messageService.sendMessage(messageRequest2);


    List<MessageResponse> messages = messageService.getNewestMessages(user1.getId());
    assertEquals(1, messages.size());
    assertEquals(messageRequest2.getMessageText(), messages.get(0).getMessageText());
  }

  @Test
  public void testGetNewestMessagesWithInvalidUser() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));

    assertThrows(AccessDeniedException.class, () -> {
      messageService.getNewestMessages(user1.getId()); // Invalid user ID
    });
  }



}
