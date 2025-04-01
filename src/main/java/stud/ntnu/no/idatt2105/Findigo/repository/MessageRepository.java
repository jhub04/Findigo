package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;

/**
 * Repository interface for managing {@link Message} entities.
 * Extends {@link JpaRepository} to provide CRUD operations.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
  /**
   * Finds all messages sent from a specific user to another specific user.
   *
   * @param fromUser The user who sent the message.
   * @param toUser The user who received the message.
   * @return A list of messages sent from the specified user to the specified user.
   */
  List<Message> findMessagesByFromUserAndToUser(User fromUser, User toUser);

  /**
   * Finds all messages sent from a specific user.
   *
   * @param fromUser The user who sent the messages.
   * @return A list of messages sent from the specified user.
   */
  List<Message> findMessagesByFromUser(User fromUser);

  /**
   * Finds all messages sent to a specific user.
   *
   * @param toUser The user who received the messages.
   * @return A list of messages sent to the specified user.
   */
  List<Message> findMessagesByToUser(User toUser);
}
