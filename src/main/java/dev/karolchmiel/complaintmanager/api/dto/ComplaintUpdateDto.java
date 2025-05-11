package dev.karolchmiel.complaintmanager.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data transfer object for updating an existing complaint")
public record ComplaintUpdateDto(
        @NotBlank(message = "Content is required")
        @Size(max = 1000, message = "Content must be no more than 1000 characters")
        @Schema(description = "Updated content of the complaint", example = "The product stopped working after one day")
        String content
) {}
