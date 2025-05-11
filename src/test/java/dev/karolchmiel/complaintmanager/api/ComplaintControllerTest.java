package dev.karolchmiel.complaintmanager.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintUpdateDto;
import dev.karolchmiel.complaintmanager.service.ComplaintReadService;
import dev.karolchmiel.complaintmanager.service.ComplaintTestData;
import dev.karolchmiel.complaintmanager.service.ComplaintWriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static dev.karolchmiel.complaintmanager.util.HttpUtils.X_FORWARDED_FOR;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ComplaintControllerTest {
    private static final String COMPLAINTS_ENDPOINT = "/complaints";
    private static final String COMPLAINT_ID_ENDPOINT = "/complaints/{id}";
    private static final String CLIENT_IP = "192.168.1.1";
    private static final ComplaintTestData TEST_DATA = ComplaintTestData.defaultData();

    @Mock
    private ComplaintReadService complaintReadService;
    @Mock
    private ComplaintWriteService complaintWriteService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ComplaintController(complaintReadService, complaintWriteService)).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    class GetComplaintTests {
        @Test
        void shouldReturnComplaint_whenComplaintExists() throws Exception {
            //given
            final var expectedDto = TEST_DATA.buildDto();
            when(complaintReadService.getComplaintById(TEST_DATA.id())).thenReturn(Optional.of(expectedDto));

            //when
            final var result = mockMvc.perform(get(COMPLAINT_ID_ENDPOINT, TEST_DATA.id()))
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            final var actualDto = objectMapper.readValue(
                    result.getResponse().getContentAsString(), 
                    ComplaintRetrievalDto.class);

            assertThat(actualDto).isEqualTo(expectedDto);
        }

        @Test
        void shouldReturnNotFound_whenComplaintDoesNotExist() throws Exception {
            //given
            when(complaintReadService.getComplaintById(TEST_DATA.id())).thenReturn(Optional.empty());

            //when/then
            mockMvc.perform(get(COMPLAINT_ID_ENDPOINT, TEST_DATA.id()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetComplaintsTests {
        @Test
        void shouldReturnAllComplaints() throws Exception {
            //given
            final var expectedDto = TEST_DATA.buildDto();
            when(complaintReadService.getAllComplaints()).thenReturn(List.of(expectedDto));

            //when
            final var actualDtos = performGetAndDeserializeList(COMPLAINTS_ENDPOINT);

            //then
            assertThat(actualDtos).containsOnly(expectedDto);
        }

        @Test
        void shouldReturnEmptyList_whenNoComplaintsExist() throws Exception {
            //given
            when(complaintReadService.getAllComplaints()).thenReturn(emptyList());

            //when
            final var actualDtos = performGetAndDeserializeList(COMPLAINTS_ENDPOINT);

            //then
            assertThat(actualDtos).isEmpty();
        }
    }

    @Nested
    class CreateComplaintTests {
        @Test
        void shouldReturnCreated_whenNewComplaintIsCreated() throws Exception {
            //given
            final var creationDto = TEST_DATA.buildCreationDto();
            final var expectedDto = TEST_DATA.buildDto();

            when(complaintWriteService.addNewOrIncrementCount(creationDto, CLIENT_IP))
                    .thenReturn(expectedDto);

            //when
            final var result = mockMvc.perform(postRequestWithClientIp(COMPLAINTS_ENDPOINT, creationDto))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/complaints/" + expectedDto.id()))
                    .andReturn();

            //then
            final var actualDto = objectMapper.readValue(
                    result.getResponse().getContentAsString(), 
                    ComplaintRetrievalDto.class);

            assertThat(actualDto).isEqualTo(expectedDto);
        }

        @Test
        void shouldReturnOk_whenDuplicateComplaintIsAdded() throws Exception {
            //given
            final var creationDto = TEST_DATA.buildCreationDto();
            final var expectedDto = new ComplaintRetrievalDto(
                    TEST_DATA.id(),
                    TEST_DATA.productId(),
                    TEST_DATA.content(),
                    TEST_DATA.creationDate(),
                    TEST_DATA.complainant(),
                    TEST_DATA.countryCode(),
                    TEST_DATA.count() + 1
            );
            when(complaintWriteService.addNewOrIncrementCount(creationDto, CLIENT_IP)).thenReturn(expectedDto);

            //when
            final var actualDto = performPostAndDeserialize(COMPLAINTS_ENDPOINT, creationDto);

            //then
            assertThat(actualDto).isEqualTo(expectedDto);
        }
    }

    @Nested
    class UpdateComplaintTests {
        @Test
        void shouldReturnNoContent_whenComplaintIsUpdated() throws Exception {
            //given
            final var updateDto = new ComplaintUpdateDto("Updated content");
            when(complaintWriteService.updateComplaint(TEST_DATA.id(), updateDto)).thenReturn(true);

            //when/then
            mockMvc.perform(patch(COMPLAINT_ID_ENDPOINT, TEST_DATA.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnNotFound_whenComplaintToUpdateDoesNotExist() throws Exception {
            //given
            final var updateDto = new ComplaintUpdateDto("Updated content");
            when(complaintWriteService.updateComplaint(TEST_DATA.id(), updateDto)).thenReturn(false);

            //when/then
            mockMvc.perform(patch(COMPLAINT_ID_ENDPOINT, TEST_DATA.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNotFound());
        }
    }

    private List<ComplaintRetrievalDto> performGetAndDeserializeList(String url) throws Exception {
        final var result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
    }

    private ComplaintRetrievalDto performPostAndDeserialize(String url, Object content) throws Exception {
        final var result = mockMvc.perform(postRequestWithClientIp(url, content))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), ComplaintRetrievalDto.class);
    }

    private MockHttpServletRequestBuilder postRequestWithClientIp(String url, Object content) throws Exception {
        return post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .with(request -> {
                    request.addHeader(X_FORWARDED_FOR, CLIENT_IP);
                    return request;
                });
    }
}
