#! /usr/bin/env bash

function checkStatus() {
	if [ ! -z "$1" ] && [ "$1" -ne "0" ]; then
		echo "Build failed..."
		exit "$1"
	fi
}

start=`date +%s`
mvn clean install -pl '!app'

checkStatus $?

cd app

mvn clean package spring-boot:repackage

checkStatus $?
end=`date +%s`
runtime=$((end - start))

cd ..

echo -e "Full Build Complete\nTime Elapsed: $runtime seconds\nExecutable JAR can be found in $PWD/app/target/"
