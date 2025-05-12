package dev.karolchmiel.complaintmanager.service;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintCreationDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintUpdateDto;
import dev.karolchmiel.complaintmanager.mapper.ComplaintMapper;
import dev.karolchmiel.complaintmanager.model.Complaint;
import dev.karolchmiel.complaintmanager.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComplaintWriteService {
    private static final Logger LOG = LoggerFactory.getLogger(ComplaintWriteService.class);

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
        LOG.info("Processing complaint for product ID: {} from complainant: {}", dto.productId(), dto.complainant());

        final var existingComplaint = complaintRepository
                .findByProductIdAndComplainant(dto.productId(), dto.complainant());

        final var complaint = existingComplaint
                .map(c -> {
                    LOG.info("Found existing complaint with ID: {}, incrementing count", c.getId());
                    return c.incrementCount();
                })
                .orElseGet(() -> {
                    LOG.info("Creating new complaint for product ID: {} from complainant: {}",
                            dto.productId(), dto.complainant());
                    return createNewComplaint(dto, remoteAddr);
                });

        final var savedComplaint = complaintRepository.save(complaint);
        LOG.info("Saved complaint with ID: {}", savedComplaint.getId());

        return complaintMapper.entityToRetrievalDto(savedComplaint);
    }

    /**
     * Updates the content of an existing complaint based on its ID.
     *
     * @param complaintId the ID of the complaint to update
     * @param dto the data transfer object containing the updated content for the complaint
     * @return true if the complaint was successfully updated, false otherwise
     */
    @Transactional
    public boolean updateComplaint(long complaintId, ComplaintUpdateDto dto) {
        LOG.info("Updating content for complaint ID: {}", complaintId);
        final var rowsUpdated = complaintRepository.updateComplaintContent(complaintId, dto.content());

        if (rowsUpdated > 0) {
            LOG.info("Successfully updated complaint ID: {}", complaintId);
            return true;
        } else {
            LOG.warn("Failed to update complaint ID: {}, complaint not found", complaintId);
            return false;
        }
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
