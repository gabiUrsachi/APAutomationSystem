package org.example;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SQSOps {
    private final static String QUEUE_NAME = "testQueue";
    public static void getQueueUrl() {

        SqsClient sqsClient = createSQSClient();
        String queueUrl = sqsClient.listQueues().queueUrls().get(0);

        System.out.println("Queue url: "+queueUrl);
    }

    private static SqsClient createSQSClient() {
        Region region = Region.US_EAST_1;

        return SqsClient.builder()
                .region(region)
                .build();
    }
}
