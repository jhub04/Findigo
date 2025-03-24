package stud.ntnu.no.idatt2105.Findigo.model;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
  private String username;
  private String password;
  private Set<Role> roles;
}