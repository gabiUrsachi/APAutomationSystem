package org.example;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Random;
import java.util.UUID;

public class SQSOps {
    private final static String QUEUE_NAME = "my-q.fifo";
    private final static String MESSAGE_GROUP_ID = "custom_msg_group_id";
    private final static String MESSAGE_DEDUPLICATION_ID = "custom_msg_deduplication_id";
    private final static String QUEUE_ARN = "arn:aws:sqs:us-east-1:847625635115:my-q.fifo";

    public static void getQueueUrl() {

        SqsClient sqsClient = createSQSClient();
        String queueUrl = sqsClient.listQueues().queueUrls().get(0);

        System.out.println("Queue url: " + queueUrl);
    }

    public static void sendMessage(String message) {
        SqsClient sqsClient = createSQSClient();

        GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(QUEUE_NAME).build());
        String queueUrl = getQueueUrlResponse.queueUrl();

        System.out.println("queue url: "+queueUrl);
        SendMessageRequest messageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .messageGroupId(MESSAGE_GROUP_ID)
                .messageDeduplicationId(UUID.randomUUID().toString())
                .build();

        sqsClient.sendMessage(messageRequest);
    }

    private static SqsClient createSQSClient() {
        Region region = Region.US_EAST_1;

        return SqsClient.builder()
                .region(region)
                .build();
    }
}
