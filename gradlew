#!/usr/bin/env bash

# This is a placeholder for the real gradlew script.
# In a real scenario, we would use 'gradle wrapper' to generate this.
# For GitHub Actions, the setup-android action or a simple 'gradle' command can also work,
# but we'll provide a basic shell script that delegates to the system gradle if available,
# or just exists so the action can find it.

if command -v gradle >/dev/null 2>&1; then
  gradle "$@"
else
  echo "Gradle not found in path. Please install Gradle or use a proper wrapper."
  exit 1
fi
