package dev.karolchmiel.complaintmanager.api.dto;

public record ComplaintCreationDto(
        Long productId,
        String content,
        String complainant
) {}
