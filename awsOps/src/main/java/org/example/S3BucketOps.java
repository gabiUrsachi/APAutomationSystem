package org.example;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class S3BucketOps {
    public static boolean checkS3ObjectExistence(String bucketName, String keyName)  {
        S3Client s3Client = createS3Client();

        try{
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(keyName).build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);

            return true;
        }
        catch (S3Exception ex){
            System.out.println("s3 bucket ops: "+ex.getMessage());
            return false;
        }
    }

    public static Resource getS3Object(String bucketName, String keyName) {
        S3Client s3Client = createS3Client();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        /// TODO
        /// handler eroare legata de inexistenta bucket-ului
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        byte[] objectData = objectBytes.asByteArray();

        return new ByteArrayResource(objectData);
    }

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

        } catch (S3Exception e) {
            /// TODO loggers
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public static void putS3Object(String bucketName, String keyName, InputStream inputStream) throws IOException {
        S3Client s3Client = createS3Client();

        try {
            Map<String, String> metadata = new HashMap<>();
            //metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    //.metadata(metadata)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(inputStream, inputStream.available());
            /// TODO
            /// preluare raspuns
            s3Client.putObject(putOb, requestBody);
            System.out.println("Successfully placed " + keyName + " into bucket " + bucketName);

        } catch (S3Exception e) {
            /// TODO
            /// logger
            System.err.println(e.getMessage());
        }
    }


    public static void copyS3Object(String sourceBucketName, String destBucketName, String sourceKeyName, String destKeyName ) throws IOException {
        S3Client s3Client = createS3Client();

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

    private static S3Client createS3Client() {
        Region region = Region.US_EAST_1;

        return S3Client.builder()
                .region(region)
                .build();
    }
}