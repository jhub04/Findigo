package stud.ntnu.no.idatt2105.Findigo.dtos.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class MessageResponse {
  private long fromUserId;
  private long toUserId;
  private String messageText;
  private long messageId;
  private boolean isRead;
  private Date sentAt;
}
