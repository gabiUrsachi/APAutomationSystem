#!/bin/bash
set -xe

sudo systemctl daemon-reload
sudo systemctl start spring-app.service