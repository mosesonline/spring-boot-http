package de.mosesonline.http.adapter.backend.first;

import de.mosesonline.http.adapter.backend.first.model.FirstBackendData;
import de.mosesonline.http.model.BackendData;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
class FirstModelMapper {

    BackendData map(FirstBackendData backendData) {
        return new BackendData(backendData.data(), backendData.dateTime().atZone(ZoneId.of("Europe/Berlin")).withZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime(), backendData.decimalData(), map(backendData.status()));
    }

    private BackendData.BackendStatus map(String status) {
        return BackendData.BackendStatus.valueOf(status.toUpperCase());
    }
}
