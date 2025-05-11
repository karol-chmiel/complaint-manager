package dev.karolchmiel.complaintmanager.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for creating a new complaint")
public record ComplaintCreationDto(
        @Schema(description = "ID of the product being complained about", example = "1001")
        Long productId,

        @Schema(description = "Content of the complaint", example = "The product is defective")
        String content,

        @Schema(description = "Name of the person making the complaint", example = "John Doe")
        String complainant
) {}
