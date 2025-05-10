package dev.karolchmiel.complaintmanager.dto;

import com.neovisionaries.i18n.CountryCode;

public record IpApiResponse(CountryCode countryCode, boolean proxy) {
}
