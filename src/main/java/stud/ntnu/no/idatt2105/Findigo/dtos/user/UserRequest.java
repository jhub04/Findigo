package stud.ntnu.no.idatt2105.Findigo.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.entities.Role;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
  private String username;
  private String password;
  private Set<Role> roles;
}
