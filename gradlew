#!/bin/sh
APP_HOME=`dirname "$0"`
if [ -x "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
  exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
else
  echo "Gradle wrapper not found. Run this from a system with gradle 8.10.2 installed."
  exit 1
fi
