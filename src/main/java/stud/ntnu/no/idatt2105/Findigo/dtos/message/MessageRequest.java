package stud.ntnu.no.idatt2105.Findigo.dtos.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
  private long fromUserId;
  private long toUserId;
  private String messageText;
}
