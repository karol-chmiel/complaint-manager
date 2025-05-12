package dev.karolchmiel.complaintmanager.service;

import com.neovisionaries.i18n.CountryCode;
import dev.karolchmiel.complaintmanager.dto.IpApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class IpGeolocationService {
    private static final String URL_PATTERN = "%s/%s?fields=countryCode,proxy";
    private static final Logger LOG = LoggerFactory.getLogger(IpGeolocationService.class);

    private final RestTemplate restTemplate;
    private final String ipApiUrl;

    public IpGeolocationService(RestTemplate restTemplate, @Value("${ipapi.url}") String ipApiUrl) {
        this.restTemplate = restTemplate;
        this.ipApiUrl = ipApiUrl;
    }

    /**
     * Retrieves the country associated with a given IP address by fetching data from an external API.
     * If the response is invalid or the API query fails, an empty {@code Optional} is returned.
     *
     * @param ipAddress the IP address
     * @return an {@code Optional} containing the {@code CountryCode} if available, otherwise an empty {@code Optional}
     */
    public Optional<CountryCode> getCountryFromIp(String ipAddress) {
        try {
            final var url = buildApiUrl(ipApiUrl, ipAddress);
            LOG.debug("Calling IP geolocation API with URL: {}", url);

            final var response = restTemplate.getForObject(url, IpApiResponse.class);
            LOG.debug("Received response from IP geolocation API: {} for IP: {}", response, ipAddress);

            if (isValidResponse(response)) {
                LOG.info("Successfully determined country {} from IP: {}", response.countryCode(), ipAddress);
                return Optional.of(response.countryCode());
            } else {
                LOG.warn("Received invalid response from IP geolocation API for IP: {}", ipAddress);
            }
        } catch (Exception e) {
            LOG.error("Error getting country from IP: {}", ipAddress, e);
        }
        return Optional.empty();
    }

    static String buildApiUrl(String ipApiUrl, String ipAddress) {
        return String.format(URL_PATTERN, ipApiUrl, ipAddress);
    }

    private static boolean isValidResponse(IpApiResponse response) {
        return response != null && response.countryCode() != null && !response.proxy();
    }
}
