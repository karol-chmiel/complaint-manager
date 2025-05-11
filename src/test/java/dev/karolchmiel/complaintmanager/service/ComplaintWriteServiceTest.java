package dev.karolchmiel.complaintmanager.service;

import com.neovisionaries.i18n.CountryCode;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintCreationDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintUpdateDto;
import dev.karolchmiel.complaintmanager.mapper.ComplaintMapper;
import dev.karolchmiel.complaintmanager.model.Complaint;
import dev.karolchmiel.complaintmanager.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintWriteServiceTest {
    private static final String COMPLAINANT_IP = "63.1.1.42";
    private static final ComplaintTestData TEST_DATA = ComplaintTestData.defaultData();

    @Mock
    private ComplaintRepository complaintRepository;
    @Mock
    private IpGeolocationService ipGeolocationService;
    @Captor
    private ArgumentCaptor<Complaint> complaintCaptor;

    private final ComplaintMapper complaintMapper = Mappers.getMapper(ComplaintMapper.class);
    private final Complaint existingComplaint = TEST_DATA.buildEntity();
    private ComplaintWriteService complaintWriteService;

    @BeforeEach
    void setUp() {
        complaintWriteService = new ComplaintWriteService(complaintRepository, complaintMapper, ipGeolocationService);

        lenient().when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> {
            final Complaint savedComplaint = invocation.getArgument(0);
            savedComplaint.setId(TEST_DATA.id());
            return savedComplaint;
        });
    }

    @Test
    void shouldCreateNewComplaint_whenComplaintDoesNotExist() {
        //given
        when(ipGeolocationService.getCountryFromIp(COMPLAINANT_IP)).thenReturn(Optional.of(CountryCode.US));
        final var dto = TEST_DATA.buildCreationDto();

        //when
        final var start = now();
        final var returnedComplaint = complaintWriteService.addNewOrIncrementCount(dto, COMPLAINANT_IP);
        final var end = now();

        //then
        final var savedComplaint = verifySavedComplaint();
        assertComplaintMatchesTestData(savedComplaint);
        assertThat(savedComplaint.getComplainantCountry()).isEqualTo(CountryCode.US);
        assertThat(savedComplaint.getCount()).isOne();
        assertThat(savedComplaint.getCreationDate()).isBetween(start, end);
        assertThat(returnedComplaint).usingRecursiveComparison().isEqualTo(savedComplaint);
    }

    @Test
    void shouldCreateComplaintWithoutCountry_whenGeolocationReturnsEmpty() {
        //given
        when(ipGeolocationService.getCountryFromIp(COMPLAINANT_IP)).thenReturn(Optional.empty());
        final var dto = TEST_DATA.buildCreationDto();

        //when
        final var start = now();
        final var returnedComplaint = complaintWriteService.addNewOrIncrementCount(dto, COMPLAINANT_IP);
        final var end = now();

        //then
        final var savedComplaint = verifySavedComplaint();
        assertComplaintMatchesTestData(savedComplaint);
        assertThat(savedComplaint.getComplainantCountry()).isNull();
        assertThat(savedComplaint.getCount()).isOne();
        assertThat(savedComplaint.getCreationDate()).isBetween(start, end);
        assertThat(returnedComplaint).usingRecursiveComparison().isEqualTo(savedComplaint);
    }

    @Test
    void shouldIncrementCount_whenDuplicateComplaintIsAdded() {
        //given
        when(complaintRepository.findByProductIdAndComplainant(TEST_DATA.productId(), TEST_DATA.complainant()))
            .thenReturn(Optional.of(existingComplaint));
        final var duplicate = new ComplaintCreationDto(TEST_DATA.productId(), "Different content", TEST_DATA.complainant());

        //when
        complaintWriteService.addNewOrIncrementCount(duplicate, COMPLAINANT_IP);

        //then
        final var savedComplaint = verifySavedComplaint();
        assertThat(savedComplaint).usingRecursiveComparison()
                .ignoringFields("count")
                .isEqualTo(existingComplaint);
        assertThat(savedComplaint.getCount()).isEqualTo(2);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0, false
            1, true
            """)
    void updateComplaint_shouldReturnTrueOnlyForExistingComplaints(int updatedRecords, boolean updateSuccessful) {
        //given
        final var newComplaintContent = "New content";
        when(complaintRepository.updateComplaintContent(TEST_DATA.id(), newComplaintContent)).thenReturn(updatedRecords);

        //when
        final var updated = complaintWriteService.updateComplaint(TEST_DATA.id(), new ComplaintUpdateDto(newComplaintContent));

        //then
        assertThat(updated).isEqualTo(updateSuccessful);
    }

    private Complaint verifySavedComplaint() {
        verify(complaintRepository).save(complaintCaptor.capture());
        return complaintCaptor.getValue();
    }

    private void assertComplaintMatchesTestData(Complaint complaint) {
        assertThat(complaint.getProductId()).isEqualTo(TEST_DATA.productId());
        assertThat(complaint.getComplainant()).isEqualTo(TEST_DATA.complainant());
        assertThat(complaint.getContent()).isEqualTo(TEST_DATA.content());
    }
}
