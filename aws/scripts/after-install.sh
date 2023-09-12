#!/bin/bash
set -xe


# Copy war file from S3 bucket to tomcat webapp folder
aws s3 cp s3://springappdeploy-webappdeploymentbucket-19q9vv6ejeohx/core-0.0.1-SNAPSHOT.jar /home/ec2-user/app/core-0.0.1-SNAPSHOT.jar


# Ensure the ownership permissions are correct.
chown -R /home/ec2-user/app