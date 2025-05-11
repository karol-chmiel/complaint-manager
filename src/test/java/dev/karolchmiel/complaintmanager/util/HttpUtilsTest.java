package dev.karolchmiel.complaintmanager.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static dev.karolchmiel.complaintmanager.util.HttpUtils.X_FORWARDED_FOR;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpUtilsTest {
    @Mock
    private HttpServletRequest request;

    @DisplayName("Client IP resolution scenarios")
    @ParameterizedTest(name = "{3} (X-Forwarded-For: {0}, RemoteAddr: {1})")
    @CsvSource(textBlock = """
            1.1.1.1          | 1.1.1.1 | 1.1.1.1 | Prefers X-Forwarded-For over remote address
            1.1.1.1, 1.1.1.2 | 1.1.1.2 | 1.1.1.1 | Uses first IP from X-Forwarded-For header
                             | 1.2.3.4 | 1.2.3.4 | Falls back to remote address when no X-Forwarded-For
                             |         |         | Returns empty when no IP available
            """, delimiterString = "|"
    )
    void shouldResolveClientIp(String xForwardedFor, String remoteAddr, String expected, String description) {
        //given
        mockRequest(xForwardedFor, remoteAddr);

        //when
        final var actualIp = HttpUtils.getClientIpAddress(request);

        //then
        assertThat(actualIp).isEqualTo(expected);
    }

    private void mockRequest(String xForwardedFor, String remoteAddr) {
        when(request.getHeader(X_FORWARDED_FOR)).thenReturn(xForwardedFor);
        if (xForwardedFor == null) {
            when(request.getRemoteAddr()).thenReturn(remoteAddr);
        }
    }
}
