package dev.karolchmiel.complaintmanager.api.dto;

import com.neovisionaries.i18n.CountryCode;

import java.time.LocalDateTime;

public record ComplaintRetrievalDto(
        Long id,
        Long productId,
        String content,
        LocalDateTime creationDate,
        String complainant,
        CountryCode complainantCountry,
        Integer count
) {}
