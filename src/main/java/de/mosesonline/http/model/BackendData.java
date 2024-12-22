package de.mosesonline.http.model;

import java.time.OffsetDateTime;

public record BackendData(String data, OffsetDateTime dateTime, float decimalData, BackendStatus status) {

   public enum BackendStatus {
        OK,
        FAILED
    }
}
