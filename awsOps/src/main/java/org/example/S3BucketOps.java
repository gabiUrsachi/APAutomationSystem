package org.example;

import org.example.awsClients.AWSS3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class S3BucketOps {
    private static final Logger logger = LoggerFactory.getLogger(S3BucketOps.class);

    public static boolean checkS3ObjectExistence(String bucketName, String keyName) {
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
            boolean itExistsObject = checkS3ObjectExistence(bucketName, keyName);
            if (!itExistsObject) {
                throw NoSuchKeyException.builder().build();
            } else {
                throw ex;
            }
        }
    }

    public static HeadBucketResponse createS3Bucket(String bucketName) {
        logger.info("[AWS - create S3 bucket] -> {}", bucketName);
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
            HeadBucketResponse headBucketResponse = waiterResponse.matched().response().orElse(null);
            logger.info("Successfully created S3 bucket {}.", headBucketResponse);

            return headBucketResponse;

        } catch (S3Exception e) {
            logger.error("[AWS - create S3 bucket] -> {}", e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public static PutObjectResponse putS3Object(String bucketName, String keyName, InputStream inputStream) throws IOException {
        S3Client s3Client = AWSS3Client.getInstance();

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(inputStream, inputStream.available());
            PutObjectResponse putObjectResponse = s3Client.putObject(putOb, requestBody);
            logger.info("Successfully placed object {} with version {} into {}.", keyName, putObjectResponse.versionId(), bucketName);

            return putObjectResponse;

        } catch (S3Exception e) {
            logger.error("[AWS - put S3 object] -> {}", e.awsErrorDetails().errorMessage());
            return null;
        }
    }


    public static CopyObjectResponse copyS3Object(String sourceBucketName, String destBucketName, String sourceKeyName, String destKeyName) throws IOException {
        S3Client s3Client = AWSS3Client.getInstance();

        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(sourceBucketName)
                .sourceKey(sourceKeyName)
                .destinationBucket(destBucketName)
                .destinationKey(destKeyName)
                .build();

        try {
            CopyObjectResponse copyRes = s3Client.copyObject(copyReq);
            logger.info("Successfully copied object {} from bucket {} to bucket {}.", sourceKeyName, sourceBucketName, destBucketName);

            return copyRes;

        } catch (S3Exception e) {
            logger.error("[AWS - copy S3 object] -> {}", e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public static List<Bucket> getS3Buckets() {
        S3Client s3Client = AWSS3Client.getInstance();

        return s3Client.listBuckets().buckets();
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