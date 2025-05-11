package dev.karolchmiel.complaintmanager.util;

import jakarta.servlet.http.HttpServletRequest;

import static io.micrometer.common.util.StringUtils.isNotBlank;

public class HttpUtils {
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private HttpUtils() {
    }

    /**
     * Extracts the client's IP address from the request.
     * If the X-Forwarded-For header is present, the method returns the first IP address from the header.
     * Otherwise, returns the remote address from the request.
     *
     * @param request the HTTP servlet request
     * @return the client's IP address, or null if the request is null
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        final var xForwardedForHeader = request.getHeader(X_FORWARDED_FOR);
        if (isNotBlank(xForwardedForHeader)) {
            return xForwardedForHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
