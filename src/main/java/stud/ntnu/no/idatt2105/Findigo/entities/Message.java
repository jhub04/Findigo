package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 * Entity representing a message exchanged between users.
 *
 * <p>
 * This entity stores information about messages sent between users, including
 * the sender, recipient, message content, timestamp, and read status.
 * </p>
 */
@Data
@Entity
@Table(name = "messages")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Message {

  /**
   * Unique identifier for the message.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private long id;

  /**
   * The user who sent the message.
   */
  @ManyToOne
  @JoinColumn(nullable = false, name = "fromUserId")
  private User fromUser;

  /**
   * The user who received the message.
   */
  @ManyToOne
  @JoinColumn(nullable = false, name = "toUserId")
  private User toUser;

  /**
   * The text content of the message.
   */
  @Column(nullable = false)
  private String messageText;

  /**
   * Timestamp indicating when the message was sent.
   * Automatically set when the message is created.
   */
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Date sentAt;

  /**
   * Boolean flag indicating whether the message has been read by the recipient.
   * Defaults to false.
   */
  @Column(nullable = false)
  private boolean isRead = false;
}
