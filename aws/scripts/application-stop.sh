#!/bin/bash
set -x

# System control will return either "active" or "inactive".
service_running=$(systemctl is-active backend.service)
if [ "$service_running" == "active" ]; then
  systemctl stop backend.service
fi