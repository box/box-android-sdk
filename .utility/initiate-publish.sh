#!/bin/bash
# This script initiates the Gradle publishing task when pushes to master occur.
# NOTE: Travis-CI can only publish SNAPSHOT versions. To release a version, you need
#       to use the internal Jenkins job.

echo Repo: $TRAVIS_REPO_SLUG Pull Request: $TRAVIS_PULL_REQUEST Branch: $TRAVIS_BRANCH
if [ "$TRAVIS_REPO_SLUG" == "box/box-android-sdk" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "tls" ]; then
  export GIT_COUNT=997788
  echo "Starting publish to Sonatype... $GIT_COUNT"

  ./gradlew uploadArchives
  RETVAL=$?

  if [ $RETVAL -eq 0 ]; then
    echo 'Completed publish!'
  else
    echo 'Publish failed.'
    return 1
  fi

fi
