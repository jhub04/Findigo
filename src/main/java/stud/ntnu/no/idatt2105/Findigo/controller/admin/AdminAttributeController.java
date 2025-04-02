package stud.ntnu.no.idatt2105.Findigo.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.service.AttributeService;

@RestController
@RequestMapping("/api/admin/attributes")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
public class AdminAttributeController {

  private final AttributeService attributeService;

  @PutMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<AttributeResponse> createCategory(
          AttributeRequest request
  ) {
    AttributeResponse createdAttribute = attributeService.createAttribute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdAttribute);
  }
}
