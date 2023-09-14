#!/bin/bash
set -xe


# Copy war file from S3 bucket to tomcat webapp folder
aws s3 cp s3://ciaooooo-webappdeploymentbucket-1sa1te44xe529/core-0.0.1-SNAPSHOT.jar /home/ec2-user/core-0.0.1-SNAPSHOT.jar
