package org.example.business.services;

import org.example.S3BucketOps;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    public Resource getFile(String bucketName, String objectName){
        //boolean isObjectExistent = S3BucketOps.checkS3ObjectExistence(bucketName, objectName);

        return S3BucketOps.getS3Object(bucketName, objectName);

    }

}
