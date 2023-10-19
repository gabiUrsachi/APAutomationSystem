package org.example;

import org.example.awsClients.AWSSQSClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

///TODO unit tests for AWS ops (in S3Ops si in SQSOps)
public class SQSOps {
    private final static String QUEUE_NAME = "fileTransferQueue.fifo";
    private final static String MESSAGE_GROUP_ID = "file_transfer_group_id";

    public static void getQueueUrl() {

        SqsClient sqsClient = AWSSQSClient.getInstance();
        String queueUrl = sqsClient.listQueues().queueUrls().get(0);

        System.out.println("Queue url: " + queueUrl);
    }

    public static void sendMessage(String message) {
        SqsClient sqsClient = AWSSQSClient.getInstance();

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

}