#!/bin/bash
set -xe

mvn clean install -DDB_HOST="${db_private_ip}"


