package org.example;

import org.example.awsClients.AWSSQSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

/// TODO unit tests for SQSOps
public class SQSOps {
    private static final Logger logger = LoggerFactory.getLogger(SQSOps.class);
    private final static String QUEUE_NAME = "fileTransferQueue.fifo";
    private final static String MESSAGE_GROUP_ID = "file_transfer_group_id";


    public static void sendMessage(String message) {
        SqsClient sqsClient = AWSSQSClient.getInstance();

        GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(QUEUE_NAME).build());
        String queueUrl = getQueueUrlResponse.queueUrl();

        SendMessageRequest messageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .messageGroupId(MESSAGE_GROUP_ID)
                .messageDeduplicationId(UUID.randomUUID().toString())
                .build();

        try{
            sqsClient.sendMessage(messageRequest);
        }
        catch (S3Exception e){
            logger.error("[AWS - send message to SQS queue] -> {}",e.awsErrorDetails().errorMessage());
        }
    }

}