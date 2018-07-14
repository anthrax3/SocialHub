#!/usr/bin/env bash
cd "$(dirname "$0")"

./gradlew -b j2objc.gradle clean :j2objcAssemble -x test
sh ./tool/buildscript.sh
sleep 5
exit 1