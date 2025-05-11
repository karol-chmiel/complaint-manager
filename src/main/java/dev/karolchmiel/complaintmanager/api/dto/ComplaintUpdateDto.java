package dev.karolchmiel.complaintmanager.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for updating an existing complaint")
public record ComplaintUpdateDto(
        @Schema(description = "Updated content of the complaint", example = "The product stopped working after one day")
        String content
) {}
