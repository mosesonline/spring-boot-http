package de.mosesonline.http.adapter.backend.first.model;

import java.time.LocalDateTime;

public record FirstBackendData(String data, LocalDateTime dateTime, float decimalData, String status) {
}
