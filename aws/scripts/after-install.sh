#!/bin/bash
set -xe


# Copy file from S3 bucket to EC2 instance

aws s3 cp s3://	backendstack-webappdeploymentbucket-1296bzuu8u2nog/core-0.0.1-SNAPSHOT.jar	 /home/ec2-user/app/core-0.0.1-SNAPSHOT.jar



stack_name="BackendStack"
db_private_ip=$(aws cloudformation describe-stacks --stack-name "$stack_name" --query "Stacks[0].Outputs[?OutputKey=='MongoEC2InstancePrivateIP'].OutputValue" --output text)

echo "
Description=spring-app

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -DDB_HOST=${db_private_ip} -jar /home/ec2-user/app/core-0.0.1-SNAPSHOT.jar

[Install]
WantedBy=multi-user.target" > /etc/systemd/system/spring-app.service