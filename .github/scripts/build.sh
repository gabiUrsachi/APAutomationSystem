#!/bin/bash
set -xe

stack_name="BackendStack"
db_private_ip=$(aws cloudformation describe-stacks --stack-name "$stack_name" --query "Stacks[0].Outputs[?OutputKey=='MongoEC2InstancePrivateIP'].OutputValue" --output text)

mvn clean install -DDB_HOST="${db_private_ip}"


