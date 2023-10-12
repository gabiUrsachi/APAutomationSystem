package org.example.business.services;

import org.example.S3BucketOps;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class FilesService {
    public Resource getFile(String bucketName, String objectName){
        return S3BucketOps.getS3Object(bucketName, objectName);
    }

}
