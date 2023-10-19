package org.example;

import org.example.awsClients.AWSS3Client;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;

public class S3BucketOps {
    public static boolean s3ObjectExists(String bucketName, String keyName) {
        try {
            S3Client s3Client = AWSS3Client.getInstance();

            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(keyName).build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (S3Exception ex) {
            return false;
        }
    }

    public static Resource getS3Object(String bucketName, String keyName) {
        S3Client s3Client = AWSS3Client.getInstance();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        try {
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            byte[] objectData = objectBytes.asByteArray();

            return new ByteArrayResource(objectData);
        } catch (NoSuchBucketException exception) {
            throw exception;
        } catch (RuntimeException ex) {
            System.out.println("Global check: " + ex.getClass() + " -> " + ex.getMessage());

            boolean objectExists = s3ObjectExists(bucketName, keyName);
            if (!objectExists) {
                throw NoSuchKeyException.builder().build();
            } else {
                throw ex;
            }
        }
    }

    public static void createS3Bucket(String bucketName) {
        S3Client s3Client = AWSS3Client.getInstance();

        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // Wait until the bucket is created and print out the response.
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);

        } catch (S3Exception e) {
            /// TODO loggers
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public static void putS3Object(String bucketName, String keyName, InputStream inputStream) throws IOException {
        S3Client s3Client = AWSS3Client.getInstance();

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(inputStream, inputStream.available());
            /// TODO preluare raspuns
            s3Client.putObject(putOb, requestBody);
            System.out.println("Successfully placed " + keyName + " into bucket " + bucketName);

        } catch (S3Exception e) {
            /// TODO logger
            System.err.println(e.getMessage());
        }
    }


    public static void copyS3Object(String sourceBucketName, String destBucketName, String sourceKeyName, String destKeyName) throws IOException {
        S3Client s3Client = AWSS3Client.getInstance();

        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(sourceBucketName)
                .sourceKey(sourceKeyName)
                .destinationBucket(destBucketName)
                .destinationKey(destKeyName)
                .build();

        try {
            CopyObjectResponse copyRes = s3Client.copyObject(copyReq);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

    }


    public static String getPresignedURL(String bucketName, String keyName) {
        S3Presigner s3Presigner = S3Presigner.create();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        // Create a GetObjectPresignRequest to specify the signature duration
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        // Generate the presigned request
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    public static List<Bucket> getBucketList() {

        S3Client s3Client = AWSS3Client.getInstance();
        List<Bucket> buckets = null;
        try {
            buckets = s3Client.listBuckets().buckets();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }

        return buckets;
    }
}