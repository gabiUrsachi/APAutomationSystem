#!/bin/bash
set -xe


# Copy war file from S3 bucket to tomcat webapp folder
aws s3 cp s3://ciaooo-webappdeploymentbucket-awn2o50qo8du/core-0.0.1-SNAPSHOT.jar /home/ec2-user/core-0.0.1-SNAPSHOT.jar
