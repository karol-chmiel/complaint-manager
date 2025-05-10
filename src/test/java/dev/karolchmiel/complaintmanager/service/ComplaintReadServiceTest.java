package dev.karolchmiel.complaintmanager.service;

import dev.karolchmiel.complaintmanager.mapper.ComplaintMapper;
import dev.karolchmiel.complaintmanager.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComplaintReadServiceTest {
    private static final long COMPLAINT_ID = 1035L;
    private static final ComplaintTestData TEST_DATA = ComplaintTestData.defaultData();

    @Mock
    private ComplaintRepository complaintRepository;
    private final ComplaintMapper complaintMapper = Mappers.getMapper(ComplaintMapper.class);
    private ComplaintReadService complaintReadService;

    @BeforeEach
    void setUp() {
        complaintReadService = new ComplaintReadService(complaintRepository, complaintMapper);
    }

    @Nested
    class GetComplaintByIdTests {
        @Test
        void shouldReturnComplaint_whenComplaintExists() {
            when(complaintRepository.findById(COMPLAINT_ID))
                    .thenReturn(Optional.of(TEST_DATA.buildEntity()));

            final var result = complaintReadService.getComplaintById(COMPLAINT_ID);

            assertThat(result).hasValue(TEST_DATA.buildDto());
        }

        @Test
        void shouldReturnEmpty_whenComplaintNotFound() {
            when(complaintRepository.findById(COMPLAINT_ID)).thenReturn(Optional.empty());

            final var result = complaintReadService.getComplaintById(COMPLAINT_ID);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetAllComplaintsTests {
        @Test
        void shouldReturnAllComplaints() {
            when(complaintRepository.findAll())
                    .thenReturn(List.of(TEST_DATA.buildEntity()));

            final var result = complaintReadService.getAllComplaints();

            assertThat(result).hasSize(1).containsOnly(TEST_DATA.buildDto());
        }
    }
}
