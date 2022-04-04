#! /usr/bin/env bash

function checkStatus() {
	if [ ! -z "$1" ] && [ "$1" -ne "0" ]; then
		echo "Build failed..."
		exit "$1"
	fi
}

function setEnv() {
	export ETHICS_EMAIL_DISABLE="true"
	export ETHICS_ANTIVIRUS_DISABLE="true"
}

setEnv

args=$1

mvn clean install $args

checkStatus $?


echo -e "Full Build Complete\nExecutable JAR can be found in $PWD/app/target/"
