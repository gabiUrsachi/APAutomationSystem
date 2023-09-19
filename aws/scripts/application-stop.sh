#!/bin/bash
set -x

# System control will return either "active" or "inactive".
app_running=$(systemctl is-active spring-app.service)
if [ "$app_running" == "active" ]; then
    sudo systemctl stop spring-app.service
fi