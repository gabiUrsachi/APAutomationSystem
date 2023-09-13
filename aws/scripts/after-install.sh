#!/bin/bash
set -xe


# Copy war file from S3 bucket to tomcat webapp folder
aws s3 cp s3://springappdeploy-webappdeploymentbucket-6e39ignkct61/core-0.0.1-SNAPSHOT.jar /home/ec2-user/app/core-0.0.1-SNAPSHOT.jar

echo "
Description=spring-app

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -DDB_HOST=172.31.40.155 -jar /home/ec2-user/app/core-0.0.1-SNAPSHOT.jar

[Install]
WantedBy=multi-user.target" > /etc/systemd/system/spring-app.service