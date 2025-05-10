package dev.karolchmiel.complaintmanager.service;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.mapper.ComplaintMapper;
import dev.karolchmiel.complaintmanager.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintReadService {
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
        return complaintRepository.findAll().stream()
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
        return complaintRepository.findById(complaintId)
                .map(complaintMapper::entityToRetrievalDto);
    }
}
