package dev.karolchmiel.complaintmanager.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data transfer object for creating a new complaint")
public record ComplaintCreationDto(
        @NotNull(message = "Product ID is required")
        @Schema(description = "ID of the product being complained about", example = "1001")
        Long productId,

        @NotBlank(message = "Content is required")
        @Size(max = 1000, message = "Content must be no more than 1000 characters")
        @Schema(description = "Content of the complaint", example = "The product is defective")
        String content,

        @NotBlank(message = "Complainant name is required")
        @Size(max = 100, message = "Complainant name must be no more than 100 characters")
        @Schema(description = "Name of the person making the complaint", example = "John Doe")
        String complainant
) {}
