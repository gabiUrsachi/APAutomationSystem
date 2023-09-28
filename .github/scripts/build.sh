#!/bin/bash
set -xe

 # Maven is used to build  and create a war file.
mvn clean install -DDB_HOST=localhost


