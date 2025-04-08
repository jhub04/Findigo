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
 * <p>
 * Stores information about messages sent between users, including
 * sender, recipient, message content, timestamp, and read status.
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
  @Column(nullable = false, updatable = false)
  private long id;

  /**
   * The user who sent the message.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_user_id", nullable = false)
  private User fromUser;

  /**
   * The user who received the message.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_user_id", nullable = false)
  private User toUser;

  /**
   * The text content of the message.
   */
  @Column(nullable = false, length = 1000)
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
