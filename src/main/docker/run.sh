#!/bin/bash

echo "start chrome......"
rpm -ivh /opt/google-chrome-stable_current_x86_64.rpm
echo "start java......"
java -jar $1
