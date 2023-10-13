package org.example.business.services;

import org.example.S3BucketOps;
import org.example.customexceptions.ObjectNotFoundException;
import org.example.utils.ErrorMessages;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class FilesService {
    public Resource getFile(String bucketName, String objectName){
        boolean isObjectExistent = S3BucketOps.checkS3ObjectExistence(bucketName, objectName);

        if(!isObjectExistent){
            System.out.println("Files service: Object doesn't exist");
            throw new ObjectNotFoundException(ErrorMessages.OBJECT_NOT_FOUND, objectName);
        }

        return S3BucketOps.getS3Object(bucketName, objectName);
    }

}
