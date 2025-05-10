package dev.karolchmiel.complaintmanager.service;

import com.neovisionaries.i18n.CountryCode;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintCreationDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.model.Complaint;

import java.time.LocalDateTime;

public record ComplaintTestData(long id, long productId, String content, String complainant,
                               LocalDateTime creationDate, CountryCode countryCode, int count) {
    
    public static ComplaintTestData defaultData() {
        return new ComplaintTestData(
                1035L,
                123L,
                "Content",
                "user-123",
                LocalDateTime.now(),
                CountryCode.US,
                1
        );
    }

    public Complaint buildEntity() {
        final var complaint = new Complaint();
        complaint.setId(id);
        complaint.setProductId(productId);
        complaint.setContent(content);
        complaint.setCreationDate(creationDate);
        complaint.setComplainant(complainant);
        complaint.setComplainantCountry(countryCode);
        complaint.setCount(count);
        return complaint;
    }

    public ComplaintRetrievalDto buildDto() {
        return new ComplaintRetrievalDto(
                id, productId, content, creationDate, complainant, countryCode, count
        );
    }
    
    public ComplaintCreationDto buildCreationDto() {
        return new ComplaintCreationDto(
                productId, content, complainant
        );
    }
}
