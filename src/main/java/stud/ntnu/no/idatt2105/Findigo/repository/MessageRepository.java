package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Message;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findMessagesByFromUserAndToUser(User fromUser, User toUser);
  List<Message> findMessagesByFromUser(User fromUser);
  List<Message> findMessagesByToUser(User toUser);
}
