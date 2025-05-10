package dev.karolchmiel.complaintmanager.service;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintCreationDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.mapper.ComplaintMapper;
import dev.karolchmiel.complaintmanager.model.Complaint;
import dev.karolchmiel.complaintmanager.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComplaintWriteService {
    private final ComplaintRepository complaintRepository;
    private final ComplaintMapper complaintMapper;
    private final IpGeolocationService ipGeolocationService;

    public ComplaintWriteService(ComplaintRepository complaintRepository,
                                ComplaintMapper complaintMapper,
                                IpGeolocationService ipGeolocationService) {
        this.complaintRepository = complaintRepository;
        this.complaintMapper = complaintMapper;
        this.ipGeolocationService = ipGeolocationService;
    }

    /**
     * Adds a new complaint record or increments the count of an existing complaint
     * based on the product ID and complainant combination. If a matching complaint
     * is found, its count is increased. If no match is found, a new complaint
     * is created and saved.
     *
     * @param dto the data transfer object containing details of the complaint
     * @param remoteAddr the IP address of the complainant used to determine the complainant's country
     * @return a {@link ComplaintRetrievalDto} representing the persisted complaint
     */
    @Transactional
    public ComplaintRetrievalDto addNewOrIncrementCount(ComplaintCreationDto dto, String remoteAddr) {
        final var complaint = complaintRepository
                .findByProductIdAndComplainant(dto.productId(), dto.complainant())
                .map(Complaint::incrementCount)
                .orElseGet(() -> createNewComplaint(dto, remoteAddr));
        final var savedComplaint = complaintRepository.save(complaint);
        return complaintMapper.entityToRetrievalDto(savedComplaint);
    }

    private Complaint createNewComplaint(ComplaintCreationDto dto, String remoteAddr) {
        final var complaint = complaintMapper.creationDtoToEntity(dto);
        complaint.setCreationDate(LocalDateTime.now());
        complaint.setCount(1);

        ipGeolocationService.getCountryFromIp(remoteAddr)
                .ifPresent(complaint::setComplainantCountry);

        return complaint;
    }
}
