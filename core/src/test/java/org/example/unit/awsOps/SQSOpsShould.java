package org.example.unit.awsOps;

import net.bytebuddy.utility.RandomString;
import org.example.SQSOps;
import org.example.awsClients.AWSSQSClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class SQSOpsShould {
    @Mock
    SqsClient sqsClient;
    static MockedStatic<AWSSQSClient> sqsClientMockedStatic;

    @BeforeClass
    public static void initialize() {
        sqsClientMockedStatic = Mockito.mockStatic(AWSSQSClient.class);
    }

    @Test
    public void returnSendMessageResponseWhenWritingToQueueIsSuccessful() {
        String message = RandomString.make();
        sqsClientMockedStatic.when(AWSSQSClient::getInstance).thenReturn(sqsClient);

        GetQueueUrlResponse getQueueUrlResponse = GetQueueUrlResponse.builder().queueUrl("queueUrl").build();
        Mockito.when(sqsClient.getQueueUrl(Mockito.any(GetQueueUrlRequest.class))).thenReturn(getQueueUrlResponse);
        SendMessageResponse sendMessageResponse = SendMessageResponse.builder().build();
        Mockito.when(sqsClient.sendMessage(Mockito.any(SendMessageRequest.class))).thenReturn(sendMessageResponse);

        SendMessageResponse actualResponse = SQSOps.sendMessage(message);

        assertEquals(sendMessageResponse, actualResponse);
        Mockito.verify(sqsClient).getQueueUrl(Mockito.any(GetQueueUrlRequest.class));
        Mockito.verify(sqsClient).sendMessage(Mockito.any(SendMessageRequest.class));
    }

    @Test
    public void returnNullWhenWritingToQueueIsNotSuccessful() {
        String message = RandomString.make();
        sqsClientMockedStatic.when(AWSSQSClient::getInstance).thenReturn(sqsClient);

        GetQueueUrlResponse getQueueUrlResponse = GetQueueUrlResponse.builder().queueUrl("queueUrl").build();
        Mockito.when(sqsClient.getQueueUrl(Mockito.any(GetQueueUrlRequest.class))).thenReturn(getQueueUrlResponse);
        S3Exception s3Exception = (S3Exception) S3Exception.builder().awsErrorDetails(AwsErrorDetails.builder().build()).build();
        Mockito.when(sqsClient.sendMessage(Mockito.any(SendMessageRequest.class))).thenThrow(s3Exception);

        SendMessageResponse actualResponse = SQSOps.sendMessage(message);

        assertNull(actualResponse);
        Mockito.verify(sqsClient).getQueueUrl(Mockito.any(GetQueueUrlRequest.class));
        Mockito.verify(sqsClient).sendMessage(Mockito.any(SendMessageRequest.class));
    }
}

