import json
import boto3
import logging
import botocore

logger = logging.getLogger()
logger.setLevel(logging.INFO)

s3 = boto3.resource('s3')


def lambda_handler(event, context):
    
    for record in event['Records']:
        body=record["body"]

        split_body = body.split("/")
        
        source_bucket_key = split_body[0]
        document_id = split_body[1]
        destination_bucket_key = split_body[2]

        documents_to_be_transfered = []
        
        for document in  s3.Bucket(name = source_bucket_key).objects.all():
            if document.key.startswith(document_id):
                documents_to_be_transfered.append(document.key)
        
        for document_name in documents_to_be_transfered:
            source_transaction={'Bucket': source_bucket_key, 'Key': document_name}
    
            try:
                response = s3.meta.client.copy(source_transaction, destination_bucket_key, document_name)
                print("File copied to the destination bucket successfully!")
            
            except botocore.exceptions.ClientError as error:
                logger.error("There was an error copying the file to the destination bucket")
                print('Error Message: {}'.format(error))
            
            except botocore.exceptions.ParamValidationError as error:
                logger.error("Missing required parameters while calling the API.")
                print('Error Message: {}'.format(error))
        
    
    return {
        'statusCode': 200,
        'body': json.dumps('Successfully transfered document')
    }
