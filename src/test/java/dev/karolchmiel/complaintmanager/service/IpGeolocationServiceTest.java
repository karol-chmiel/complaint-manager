package dev.karolchmiel.complaintmanager.service;

import com.neovisionaries.i18n.CountryCode;
import dev.karolchmiel.complaintmanager.dto.IpApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpGeolocationServiceTest {

    private static final String TEST_IP = "51.134.2.4";
    private static final String TEST_API_URL = "http://ip-api.com/json";
    private static final String EXPECTED_REQUEST_URL = IpGeolocationService.buildApiUrl(TEST_API_URL, TEST_IP);

    @Mock
    private RestTemplate restTemplate;
    private IpGeolocationService ipGeolocationService;

    @BeforeEach
    void setUp() {
        ipGeolocationService = new IpGeolocationService(restTemplate, TEST_API_URL);
    }

    @Nested
    class SuccessScenarios {
        @ParameterizedTest
        @EnumSource(value = CountryCode.class, mode = EnumSource.Mode.INCLUDE, names = {"PL", "FR"})
        void shouldReturnCountryCode_whenValidResponse(CountryCode countryCode) {
            mockIpApiResponse(countryCode, false);
            assertCountryCode(countryCode);
        }
    }

    @Nested
    class ErrorScenarios {
        @Test
        void shouldReturnEmpty_whenNullResponse() {
            when(restTemplate.getForObject(EXPECTED_REQUEST_URL, IpApiResponse.class))
                    .thenReturn(null);
            assertEmptyResponse();
        }

        @Test
        void shouldReturnEmpty_whenNullCountryCode() {
            mockIpApiResponse(null, false);
            assertEmptyResponse();
        }

        @Test
        void shouldReturnEmpty_whenProxyDetected() {
            mockIpApiResponse(CountryCode.DE, true);
            assertEmptyResponse();
        }

        @Test
        void shouldReturnEmpty_whenApiCallFails() {
            when(restTemplate.getForObject(EXPECTED_REQUEST_URL, IpApiResponse.class))
                    .thenThrow(new RuntimeException("API Error"));
            assertEmptyResponse();
        }
    }

    private void mockIpApiResponse(CountryCode country, boolean isProxy) {
        when(restTemplate.getForObject(EXPECTED_REQUEST_URL, IpApiResponse.class))
                .thenReturn(new IpApiResponse(country, isProxy));
    }

    private void assertCountryCode(CountryCode expected) {
        var result = ipGeolocationService.getCountryFromIp(TEST_IP);
        assertThat(result).hasValue(expected);
    }

    private void assertEmptyResponse() {
        var result = ipGeolocationService.getCountryFromIp(TEST_IP);
        assertThat(result).isEmpty();
    }
}
