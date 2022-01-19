#! /usr/bin/env bash

function checkStatus() {
	if [ ! -z "$1" ] && [ "$1" -ne "0" ]; then
		echo "Tests failed..."
		exit "$1"
	fi
}

mvn test

checkStatus $?
cd ..

echo "Testing completed successfully."
