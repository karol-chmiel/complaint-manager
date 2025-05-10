package dev.karolchmiel.complaintmanager.mapper;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.model.Complaint;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {
    ComplaintRetrievalDto entityToRetrievalDto(Complaint entity);
}