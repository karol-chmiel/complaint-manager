package dev.karolchmiel.complaintmanager.mapper;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintCreationDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.model.Complaint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "complainantCountry", ignore = true)
    @Mapping(target = "count", ignore = true)
    Complaint creationDtoToEntity(ComplaintCreationDto dto);

    ComplaintRetrievalDto entityToRetrievalDto(Complaint entity);
}