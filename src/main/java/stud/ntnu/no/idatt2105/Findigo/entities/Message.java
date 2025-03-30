package stud.ntnu.no.idatt2105.Findigo.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="messages")
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private long id;

  @ManyToOne
  @JoinColumn(nullable = false, name="userId")
  private User fromUser;

  @ManyToOne
  @JoinColumn(nullable = false, name="userId")
  private User toUser;

  @Column(nullable = false)
  private String messageText;

}
