package stud.ntnu.no.idatt2105.Findigo.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.service.AttributeService;

import java.util.List;

/**
 * Admin controller for managing attributes.
 *
 * <p>Provides endpoints for administrators to:
 * <ul>
 *     <li>Create new attributes</li>
 *     <li>Retrieve all attributes</li>
 *     <li>Update existing attributes</li>
 *     <li>Delete attributes</li>
 * </ul>
 * Access is restricted to users with the 'ADMIN' role.
 * </p>
 */
@RestController
@RequestMapping("/api/admin/attributes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Attributes", description = "Operations for managing attributes by admin users")
public class AdminAttributeController {

  private static final Logger logger = LogManager.getLogger(AdminAttributeController.class);

  private final AttributeService attributeService;

  /**
   * Creates a new attribute.
   *
   * @param request the attribute data
   * @return the created attribute with HTTP 201 status
   */
  @Operation(summary = "Create a new attribute", description = "Creates a new attribute with the provided data. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Attribute successfully created"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<AttributeResponse> create(
          @Validated @RequestBody AttributeRequest request
  ) {
    logger.info("Admin: Creating new attribute with name '{}'", request.getName());
    AttributeResponse createdAttribute = attributeService.createAttribute(request);
    logger.info("Admin: Attribute created with ID {}", createdAttribute.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdAttribute);
  }

  /**
   * Retrieves all attributes.
   *
   * @return the list of all attributes
   */
  @Operation(summary = "Get all attributes", description = "Retrieves a list of all existing attributes. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "List of attributes retrieved successfully"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping
  public ResponseEntity<List<AttributeResponse>> findAll() {
    logger.info("Admin: Fetching all attributes");
    List<AttributeResponse> attributes = attributeService.getAllAttributes();
    logger.info("Admin: Retrieved {} attributes", attributes.size());
    return ResponseEntity.ok(attributes);
  }

  /**
   * Updates an existing attribute.
   *
   * @param attributeId the ID of the attribute to update
   * @param request     the updated attribute data
   * @return a confirmation message
   */
  @Operation(summary = "Update an existing attribute", description = "Updates an existing attribute by ID. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Attribute successfully updated"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "404", description = "Attribute not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PutMapping("/{attributeId}")
  public ResponseEntity<String> update(
          @Parameter(description = "ID of the attribute to update", example = "1")
          @PathVariable Long attributeId,
          @Validated @RequestBody AttributeRequest request
  ) {
    logger.info("Admin: Updating attribute with ID {}", attributeId);
    attributeService.editAttribute(attributeId, request);
    logger.info("Admin: Attribute updated with ID {}", attributeId);
    return ResponseEntity.ok("Attribute successfully updated");
  }

  /**
   * Deletes an existing attribute.
   *
   * @param attributeId the ID of the attribute to delete
   * @return a confirmation message
   */
  @Operation(summary = "Delete an attribute", description = "Deletes an attribute by ID. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Attribute successfully deleted"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "404", description = "Attribute not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{attributeId}")
  public ResponseEntity<String> delete(
          @Parameter(description = "ID of the attribute to delete", example = "1")
          @PathVariable Long attributeId
  ) {
    logger.info("Admin: Deleting attribute with ID {}", attributeId);
    attributeService.deleteAttribute(attributeId);
    logger.info("Admin: Attribute deleted with ID {}", attributeId);
    return ResponseEntity.ok("Attribute successfully deleted");
  }
}
