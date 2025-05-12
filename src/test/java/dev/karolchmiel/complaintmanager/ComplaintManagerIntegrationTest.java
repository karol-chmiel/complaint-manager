package dev.karolchmiel.complaintmanager;

import dev.karolchmiel.complaintmanager.api.dto.ComplaintRetrievalDto;
import dev.karolchmiel.complaintmanager.api.dto.ComplaintUpdateDto;
import dev.karolchmiel.complaintmanager.service.ComplaintTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ComplaintManagerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:";
    private static final String COMPLAINTS_ENDPOINT = "/complaints";
    private static final ComplaintTestData TEST_DATA = ComplaintTestData.defaultData();

    @Test
    void shouldSuccessfullyPerformComplaintLifecycle() {
        //Create complaint
        final var creationDto = TEST_DATA.buildCreationDto();
        final var createResponse = restTemplate.postForEntity(
                BASE_URL + port + COMPLAINTS_ENDPOINT,
                creationDto,
                ComplaintRetrievalDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().productId()).isEqualTo(creationDto.productId());
        assertThat(createResponse.getBody().content()).isEqualTo(creationDto.content());
        assertThat(createResponse.getBody().complainant()).isEqualTo(creationDto.complainant());
        assertThat(createResponse.getBody().count()).isEqualTo(1);

        //Get all complaints
        final var getAllResponse = restTemplate.exchange(
                BASE_URL + port + COMPLAINTS_ENDPOINT,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ComplaintRetrievalDto>>() {
                });

        assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAllResponse.getBody()).isNotNull();
        assertThat(getAllResponse.getBody()).hasSize(1);
        assertThat(getAllResponse.getBody().getFirst().productId()).isEqualTo(creationDto.productId());

        //Submit duplicate complaint
        final var duplicateResponse = restTemplate.postForEntity(
                BASE_URL + port + COMPLAINTS_ENDPOINT,
                creationDto,
                ComplaintRetrievalDto.class);

        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duplicateResponse.getBody()).isNotNull();
        assertThat(duplicateResponse.getBody().productId()).isEqualTo(creationDto.productId());
        assertThat(duplicateResponse.getBody().content()).isEqualTo(creationDto.content());
        assertThat(duplicateResponse.getBody().complainant()).isEqualTo(creationDto.complainant());
        assertThat(duplicateResponse.getBody().count()).isEqualTo(2);

        //Update complaint
        final var complaintId = createResponse.getBody().id();
        final var updateDto = new ComplaintUpdateDto("Updated content");

        final var updateResponse = restTemplate.exchange(
                BASE_URL + port + COMPLAINTS_ENDPOINT + "/" + complaintId,
                HttpMethod.PATCH,
                new HttpEntity<>(updateDto),
                Void.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        final var getResponse = restTemplate.getForEntity(
                BASE_URL + port + COMPLAINTS_ENDPOINT + "/" + complaintId,
                ComplaintRetrievalDto.class);
        assertThat(getResponse.getBody())
                .extracting(ComplaintRetrievalDto::content)
                .isEqualTo(updateDto.content());
    }
}
