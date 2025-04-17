package de.mosesonline.http.session;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.UUID;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.*;

@Configuration(proxyBeanMethods = false)
@RegisterReflection(classNames = "org.zalando.logbook.json.JsonHttpLogFormatter$JsonBody", memberCategories =
        { MemberCategory.INVOKE_PUBLIC_METHODS })
public class SessionConfiguration {

    @Value("${aws.dynamodb.accessKey}")
    private String accessKey;

    @Value("${aws.dynamodb.secretKey}")
    private String secretKey;

    @Value("${aws.dynamodb.region}")
    private String region;

    @Value("${aws.dynamodb.endpoint}")
    private String endpoint;
     static final TableSchema<DynamoDbSessionData> TABLE_SCHEMA =
            StaticTableSchema.builder(DynamoDbSessionData.class)
                    .newItemSupplier(DynamoDbSessionData::new)
                    .addAttribute(UUID.class, a -> a.name("id")
                            .getter(DynamoDbSessionData::getId)
                            .setter(DynamoDbSessionData::setId)
                            .tags(primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("sort")
                            .getter(DynamoDbSessionData::getSessionData)
                            .setter(DynamoDbSessionData::setSessionData))
                    .build();


    @Bean
    DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}
