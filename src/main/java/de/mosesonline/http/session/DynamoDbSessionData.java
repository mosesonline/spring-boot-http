package de.mosesonline.http.session;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;
import java.util.UUID;

@DynamoDbBean
public class DynamoDbSessionData {
    private UUID id;
    private String sessionData;

    static DynamoDbSessionData from(UUID id,
                                    String sessionData) {
        final var data = new DynamoDbSessionData();
        data.id = id;
        data.sessionData = sessionData;
        return data;
    }

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public String getSessionData() {
        return sessionData;
    }

    public void setSessionData(String sessionData) {
        this.sessionData = sessionData;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DynamoDbSessionData) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.sessionData, that.sessionData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sessionData);
    }

    @Override
    public String toString() {
        return "DynamoDbSessionData[" +
                "id=" + id + ", " +
                "sessionData=" + sessionData + ']';
    }

}
