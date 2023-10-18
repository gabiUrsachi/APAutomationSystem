package org.example.awsClients;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public class AWSSQSClient {
    private static final Region region = Region.US_EAST_1;
    private static SqsClient instance;

    public static synchronized SqsClient getInstance() {
        if (instance == null) {
            instance = SqsClient.builder()
                    .region(region)
                    .build();
        }

        return instance;
    }
}
