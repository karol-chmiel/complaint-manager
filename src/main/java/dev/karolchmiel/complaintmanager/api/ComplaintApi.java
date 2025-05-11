package dev.karolchmiel.complaintmanager.api;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintCreationDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Complaints", description = "Complaint management API")
@RequestMapping("/complaints")
public interface ComplaintApi {

    @Operation(summary = "Get a complaint by ID", description = "Returns a complaint based on the ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaint found",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ComplaintRetrievalDto.class))),
            @ApiResponse(responseCode = "404", description = "Complaint not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<ComplaintRetrievalDto> getComplaint(
            @Parameter(description = "ID of the complaint to be retrieved") @PathVariable int id);

    @Operation(summary = "Get all complaints", description = "Returns a list of all complaints")
    @ApiResponse(responseCode = "200", description = "List of complaints retrieved successfully",
            content = @Content(mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ComplaintRetrievalDto.class)))
    @GetMapping
    List<ComplaintRetrievalDto> getComplaints();

    @Operation(summary = "Create a new complaint",
            description = "Creates a new complaint or increments the count if a similar complaint exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Complaint created successfully",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ComplaintRetrievalDto.class))),
            @ApiResponse(responseCode = "200", description = "Similar complaint found, count incremented",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ComplaintRetrievalDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    ResponseEntity<ComplaintRetrievalDto> createComplaint(
            @Parameter(description = "Complaint information for creation", required = true)
            @Valid @RequestBody ComplaintCreationDto dto,
            HttpServletRequest request);

    @Operation(summary = "Update a complaint", description = "Updates the content of an existing complaint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Complaint updated successfully"),
            @ApiResponse(responseCode = "404", description = "Complaint not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Map.class)))
    })
    @PatchMapping("/{id}")
    ResponseEntity<Void> updateComplaint(
            @Parameter(description = "ID of the complaint to update") @PathVariable long id,
            @Parameter(description = "Updated complaint information", required = true) @Valid @RequestBody ComplaintUpdateDto dto);
}
