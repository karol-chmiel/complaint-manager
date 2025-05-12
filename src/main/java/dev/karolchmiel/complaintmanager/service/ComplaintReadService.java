package dev.karolchmiel.complaintmanager.service;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.mapper.ComplaintMapper;
import dev.karolchmiel.complaintmanager.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintReadService {
    private static final Logger LOG = LoggerFactory.getLogger(ComplaintReadService.class);

    private final ComplaintRepository complaintRepository;
    private final ComplaintMapper complaintMapper;

    public ComplaintReadService(ComplaintRepository complaintRepository,
                                ComplaintMapper complaintMapper) {
        this.complaintRepository = complaintRepository;
        this.complaintMapper = complaintMapper;
    }

    /**
     * Retrieves all complaints from the system.
     *
     * @return list of all complaints converted to DTOs
     */
    public List<ComplaintRetrievalDto> getAllComplaints() {
        LOG.info("Retrieving all complaints");
        final var complaints = complaintRepository.findAll();
        LOG.debug("Found {} complaints in the database", complaints.size());

        return complaints.stream()
                .map(complaintMapper::entityToRetrievalDto)
                .toList();
    }

    /**
     * Finds a specific complaint by its ID.
     *
     * @param complaintId the ID of the complaint to retrieve
     * @return the complaint if found, empty Optional otherwise
     */
    public Optional<ComplaintRetrievalDto> getComplaintById(long complaintId) {
        LOG.info("Retrieving complaint with ID: {}", complaintId);

        final var complaint = complaintRepository.findById(complaintId);

        if (complaint.isPresent()) {
            LOG.debug("Found complaint with ID: {}", complaintId);
            return complaint.map(complaintMapper::entityToRetrievalDto);
        } else {
            LOG.warn("Complaint with ID: {} not found", complaintId);
            return Optional.empty();
        }
    }
}
