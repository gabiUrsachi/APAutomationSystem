package org.example.awsClients;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AWSS3Client {
    private static final Region region = Region.US_EAST_1;
    private static S3Client instance;

    public static synchronized S3Client getInstance() {
        if (instance == null) {
            instance = S3Client.builder()
                    .region(region)
                    .build();
        }

        return instance;
    }
}
