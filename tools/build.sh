#! /usr/bin/env bash

function checkStatus() {
	if [ ! -z "$1" ] && [ "$1" -ne "0" ]; then
		echo "Build failed..."
		exit "$1"
	fi
} 

mvn clean install -pl '!app'

checkStatus $?

cd app

mvn clean package spring-boot:repackage

checkStatus $?

cd ..

echo "Build complete. Executable JAR can be found in $PWD/app/target/"
