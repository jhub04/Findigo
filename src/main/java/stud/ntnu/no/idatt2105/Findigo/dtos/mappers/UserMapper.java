package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;

@Component
public class UserMapper {

  public UserResponse toDTO(User user) {
    return new UserResponse(user.getId(), user.getUsername());
  }
}
