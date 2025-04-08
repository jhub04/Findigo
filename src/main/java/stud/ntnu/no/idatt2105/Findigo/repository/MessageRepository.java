package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;

/**
 * Repository interface for managing {@link Message} entities.
 * <p>
 * Provides CRUD operations and custom queries for messages exchanged between users.
 * </p>
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  /**
   * Finds all messages sent from a specific user to another specific user.
   *
   * @param fromUser the user who sent the messages
   * @param toUser   the user who received the messages
   * @return a list of messages sent from {@code fromUser} to {@code toUser}
   */
  List<Message> findMessagesByFromUserAndToUser(User fromUser, User toUser);

  /**
   * Finds all messages sent from a specific user.
   *
   * @param fromUser the user who sent the messages
   * @return a list of messages sent by {@code fromUser}
   */
  List<Message> findMessagesByFromUser(User fromUser);

  /**
   * Finds all messages sent to a specific user.
   *
   * @param toUser the user who received the messages
   * @return a list of messages received by {@code toUser}
   */
  List<Message> findMessagesByToUser(User toUser);
}
