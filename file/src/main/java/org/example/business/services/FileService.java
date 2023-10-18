package org.example.business.services;

import org.example.S3BucketOps;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    public Resource getFile(String bucketName, String objectName){
//        boolean isObjectExistent = S3BucketOps.checkS3ObjectExistence(bucketName, objectName);
//
//        if(!isObjectExistent){
//            throw new ResourceNotFoundException(ErrorMessages.S3_OBJECT_NOT_FOUND, objectName);
//        }

        return S3BucketOps.getS3Object(bucketName, objectName);
    }

}
