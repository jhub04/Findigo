package stud.ntnu.no.idatt2105.Findigo.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class EditUserDto {
  private Long id;
  private String username;
  private String password;
}
