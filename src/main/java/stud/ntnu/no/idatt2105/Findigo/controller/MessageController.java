package stud.ntnu.no.idatt2105.Findigo.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.message.MessageResponse;
import stud.ntnu.no.idatt2105.Findigo.service.MessageService;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
  private MessageService messageService;
  private static final Logger logger = LogManager.getLogger(MessageController.class);

  @PostMapping("/send")
  public ResponseEntity<?> sendNewMessage(@RequestBody MessageRequest messageRequest) {
    logger.info("Sending new message from userId " + messageRequest.getFromUserId() + " to userId " + messageRequest.getToUserId());
    MessageResponse messageResponse = messageService.sendMessage(messageRequest);
    logger.info("Message sent from userId " + messageRequest.getFromUserId() + " to userId " + messageRequest.getToUserId());
    return ResponseEntity.ok(messageResponse);
  }

  @GetMapping()


}
