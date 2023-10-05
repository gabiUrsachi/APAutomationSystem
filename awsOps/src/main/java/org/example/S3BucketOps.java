package org.example;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class S3BucketOps {

    public static void createS3Bucket(String bucketName) {
        S3Client s3Client = createS3Client();

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
            System.out.println(bucketName + " is ready");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public static void putS3Object(String bucketName, String uri, InputStream inputStream) throws IOException {
        S3Client s3Client = createS3Client();

        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uri)
                    .metadata(metadata)
                    .build();

            RequestBody requestBody =  RequestBody.fromInputStream(inputStream, inputStream.available());
            s3Client.putObject(putOb, requestBody);
            System.out.println("Successfully placed " + uri +" into bucket "+bucketName);

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
        }
    }

    ///TODO
    /// maybe Singleton pattern for s3 client creation
    private static S3Client createS3Client() {
        Region region = Region.US_EAST_1;

        return S3Client.builder()
                .region(region)
                .build();
    }
}