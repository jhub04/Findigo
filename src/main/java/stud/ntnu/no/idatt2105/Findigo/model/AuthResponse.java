package stud.ntnu.no.idatt2105.Findigo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class AuthResponse {
  private String token;
}