package dev.karolchmiel.complaintmanager.api;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintCreationDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintUpdateDto;
import dev.karolchmiel.complaintmanager.service.ComplaintReadService;
import dev.karolchmiel.complaintmanager.service.ComplaintWriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static dev.karolchmiel.complaintmanager.util.HttpUtils.getClientIpAddress;


@RestController
@RequestMapping("/complaints")
public class ComplaintController {
    private final ComplaintReadService complaintReadService;
    private final ComplaintWriteService complaintWriteService;

    public ComplaintController(ComplaintReadService complaintReadService, ComplaintWriteService complaintWriteService) {
        this.complaintReadService = complaintReadService;
        this.complaintWriteService = complaintWriteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintRetrievalDto> getComplaint(@PathVariable int id) {
        return complaintReadService.getComplaintById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ComplaintRetrievalDto> getComplaints() {
        return complaintReadService.getAllComplaints();
    }

    @PostMapping
    public ResponseEntity<ComplaintRetrievalDto> createComplaint(@RequestBody ComplaintCreationDto dto,
                                                 HttpServletRequest request) {
        final var savedComplaint = complaintWriteService.addNewOrIncrementCount(dto, getClientIpAddress(request));
        if (savedComplaint.count() == 1) {
            final var location = buildComplaintUri(savedComplaint);
            return ResponseEntity.created(location).body(savedComplaint);
        }
        return ResponseEntity.ok(savedComplaint);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateComplaint(@PathVariable long id, @RequestBody ComplaintUpdateDto dto) {
        final var updated = complaintWriteService.updateComplaint(id, dto);
        if (updated) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private static URI buildComplaintUri(ComplaintRetrievalDto savedComplaint) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedComplaint.id())
                .toUri();
    }
}
