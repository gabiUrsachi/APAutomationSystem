#!/bin/bash
set -xe


# Copy war file from S3 bucket to tomcat webapp folder
aws s3 cp s3://meuf-webappdeploymentbucket-3olo9s6vq757/core-0.0.1-SNAPSHOT.jar /home/ec2-user/core-0.0.1-SNAPSHOT.jar
