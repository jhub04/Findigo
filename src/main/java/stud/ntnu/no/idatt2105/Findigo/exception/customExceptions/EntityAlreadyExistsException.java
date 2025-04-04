package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

import lombok.Getter;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {
  private final CustomErrorMessage errorMessage;

  public EntityAlreadyExistsException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
  }
}
