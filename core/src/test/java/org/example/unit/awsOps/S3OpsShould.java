package org.example.unit.awsOps;

import io.findify.s3mock.S3Mock;
import net.bytebuddy.utility.RandomString;
import org.example.S3BucketOps;
import org.example.awsClients.AWSS3Client;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.atLeast;

@RunWith(MockitoJUnitRunner.class)
public class S3OpsShould {
    static S3Client s3Client;
    static S3Mock api;
    static MockedStatic<AWSS3Client> awsS3ClientMockedStatic;

    @Mock
    InputStream inputStream;

    @BeforeClass
    public static void initialize() {
        api = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
        api.start();

        s3Client = S3Client.builder()
                .forcePathStyle(true)
                .endpointOverride(URI.create("http://localhost:8001"))
                .region(Region.US_EAST_1)
                .build();

        awsS3ClientMockedStatic = Mockito.mockStatic(AWSS3Client.class);
    }

    @Test
    public void successfullyCreateANewBucket() {
        String bucketName = RandomString.make().toLowerCase();

        awsS3ClientMockedStatic.when(AWSS3Client::getInstance).thenReturn(s3Client);

        HeadBucketResponse createdBucket = S3BucketOps.createS3Bucket(bucketName);
        List<Bucket> allBuckets = S3BucketOps.getS3Buckets();

        Assertions.assertNotNull(createdBucket);
        Assertions.assertTrue(allBuckets.stream().anyMatch(bucket -> bucket.name().equals(bucketName)));
        awsS3ClientMockedStatic.verify(AWSS3Client::getInstance, atLeast(1));
    }

    @Test
    public void returnNullWhenTryingToPutObjectToNonexistentBucket() throws IOException {
        String bucketName = RandomString.make().toLowerCase();
        String keyName = RandomString.make().toLowerCase();

        awsS3ClientMockedStatic.when(AWSS3Client::getInstance).thenReturn(s3Client);

        PutObjectResponse putObjectResponse = S3BucketOps.putS3Object(bucketName, keyName, inputStream);

        Assertions.assertNull(putObjectResponse);
        awsS3ClientMockedStatic.verify(AWSS3Client::getInstance, atLeast(1));
    }


    @Test
    public void returnNonNullObjectWhenTryingToPutToExistentBucket() throws IOException {
        String bucketName = RandomString.make().toLowerCase();
        String keyName = RandomString.make().toLowerCase();

        awsS3ClientMockedStatic.when(AWSS3Client::getInstance).thenReturn(s3Client);

        S3BucketOps.createS3Bucket(bucketName);
        PutObjectResponse putObjectResponse = S3BucketOps.putS3Object(bucketName, keyName, inputStream);

        Assertions.assertNotNull(putObjectResponse);
        awsS3ClientMockedStatic.verify(AWSS3Client::getInstance, atLeast(1));
    }

    @Test
    public void throwNoSuchBucketExceptionWhenQueryingFromNonExistentBucket() {
        String bucketName = RandomString.make().toLowerCase();
        String keyName = RandomString.make().toLowerCase();

        awsS3ClientMockedStatic.when(AWSS3Client::getInstance).thenReturn(s3Client);

        Assertions.assertThrows(NoSuchBucketException.class, () -> S3BucketOps.getS3Object(bucketName, keyName));
        awsS3ClientMockedStatic.verify(AWSS3Client::getInstance, atLeast(1));
    }

    @Test
    public void throwNoSuchKeyExceptionWhenQueryingNonExistentObjectFromExistentBucket() {
        String bucketName = RandomString.make().toLowerCase();
        String keyName = RandomString.make().toLowerCase();

        awsS3ClientMockedStatic.when(AWSS3Client::getInstance).thenReturn(s3Client);

        S3BucketOps.createS3Bucket(bucketName);

        Assertions.assertThrows(NoSuchKeyException.class, () -> S3BucketOps.getS3Object(bucketName, keyName));
        awsS3ClientMockedStatic.verify(AWSS3Client::getInstance, atLeast(1));
    }

    @Test
    public void successfullyRetrieveExistentObjectFromExistentBucket() throws IOException {
        String bucketName = RandomString.make().toLowerCase();
        String keyName = RandomString.make().toLowerCase();

        awsS3ClientMockedStatic.when(AWSS3Client::getInstance).thenReturn(s3Client);

        S3BucketOps.createS3Bucket(bucketName);
        S3BucketOps.putS3Object(bucketName, keyName, inputStream);
        Resource resource = S3BucketOps.getS3Object(bucketName, keyName);

        Assertions.assertTrue(resource.exists());
        awsS3ClientMockedStatic.verify(AWSS3Client::getInstance, atLeast(1));
    }

    @Test
    public void successfullyCopyObjectFromOneBucketToAnother() throws IOException {
        String sourceBucketName = RandomString.make().toLowerCase();
        String destBucketName = RandomString.make().toLowerCase();
        String keyName = RandomString.make().toLowerCase();

        awsS3ClientMockedStatic.when(AWSS3Client::getInstance).thenReturn(s3Client);

        S3BucketOps.createS3Bucket(sourceBucketName);
        S3BucketOps.createS3Bucket(destBucketName);
        S3BucketOps.putS3Object(sourceBucketName, keyName, inputStream);

        S3BucketOps.copyS3Object(sourceBucketName, destBucketName, keyName, keyName);
        Resource resource = S3BucketOps.getS3Object(destBucketName, keyName);

        Assertions.assertTrue(resource.exists());
        awsS3ClientMockedStatic.verify(AWSS3Client::getInstance, atLeast(1));
    }

    @Test
    public void returnNullWhenTryingToCopyNonExistentObject() throws IOException {
        String sourceBucketName = RandomString.make().toLowerCase();
        String destBucketName = RandomString.make().toLowerCase();
        String keyName = RandomString.make().toLowerCase();

        awsS3ClientMockedStatic.when(AWSS3Client::getInstance).thenReturn(s3Client);

        S3BucketOps.createS3Bucket(sourceBucketName);
        S3BucketOps.createS3Bucket(destBucketName);

        CopyObjectResponse copyObjectResponse = S3BucketOps.copyS3Object(sourceBucketName, destBucketName, keyName, keyName);

        Assertions.assertNull(copyObjectResponse);
        awsS3ClientMockedStatic.verify(AWSS3Client::getInstance, atLeast(1));
    }
}
