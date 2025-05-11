package dev.karolchmiel.complaintmanager.api.dto;

import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object for retrieving complaint information")
public record ComplaintRetrievalDto(
        @Schema(description = "Unique identifier of the complaint", example = "1")
        Long id,

        @Schema(description = "ID of the product being complained about", example = "1001")
        Long productId,

        @Schema(description = "Content of the complaint", example = "The product is defective")
        String content,

        @Schema(description = "Date and time when the complaint was created", example = "2023-05-20T14:30:00")
        LocalDateTime creationDate,

        @Schema(description = "Name of the person making the complaint", example = "John Doe")
        String complainant,

        @Schema(description = "Country code of the complainant's location", example = "US")
        CountryCode complainantCountry,

        @Schema(description = "Number of times this complaint has been reported", example = "1")
        Integer count
) {}
