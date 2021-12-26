#! /usr/bin/env bash

LOG="$PWD/build.log"

function checkStatus() {
	if [ ! -z "$1" ] && [ "$1" -ne "0" ]; then
		echo "Build failed, see $LOG"
		exit "$1"
	fi
} 

mvn clean install >> "$LOG" 2>&1

checkStatus $?

cd app

mvn package spring-boot:repackage >> "$LOG" 2>&1

checkStatus $?

cd ..

echo "Build complete. Executable JAR can be found in $PWD/app/target/"
