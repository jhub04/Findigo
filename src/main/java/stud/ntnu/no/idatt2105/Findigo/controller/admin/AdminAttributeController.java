package stud.ntnu.no.idatt2105.Findigo.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.service.AttributeService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/attributes")
@PreAuthorize("hasRole('Admin')") //TODO: caps admin?
@RequiredArgsConstructor
public class AdminAttributeController {

  private final AttributeService attributeService;

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<AttributeResponse> createCategory(
          @RequestBody AttributeRequest request
  ) {
    AttributeResponse createdAttribute = attributeService.createAttribute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdAttribute);
  }

  @GetMapping()
  public ResponseEntity<List<AttributeResponse>> getAllAttributes() {
    List<AttributeResponse> attributes = attributeService.getAllAttributes();
    return ResponseEntity.ok(attributes);
  }

  @PutMapping("/edit/{attributeId}")
  public ResponseEntity<String> editAttribute(
          @PathVariable Long attributeId,
          @RequestBody AttributeRequest request
  ) {
    attributeService.editAttribute(attributeId, request);
    return ResponseEntity.ok("Attribute successfully edited");
  }

  @DeleteMapping("/{attributeId}")
  public ResponseEntity<String> deleteAttribute(
          @PathVariable Long attributeId
  ) {
    attributeService.deleteAttribute(attributeId);
    return ResponseEntity.ok("Attribute successfully deleted");
  }
}
