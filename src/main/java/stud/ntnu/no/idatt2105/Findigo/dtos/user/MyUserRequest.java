package stud.ntnu.no.idatt2105.Findigo.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for editing own profile, including username, password, and phone number.")
public class MyUserRequest {

  @NotBlank(message = "Username cannot be blank")
  @Schema(description = "The username of the user", example = "john_doe")
  private String username;

  @NotBlank(message = "Password cannot be blank")
  @Schema(description = "The password of the user", example = "securePassword123")
  private String password;

  @NotNull(message = "Phone number cannot be null")
  @Schema(description = "The phone number of the user", example = "12345678")
  private Long phoneNumber;
}
