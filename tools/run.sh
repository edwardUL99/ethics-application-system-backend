#! /usr/bin/env bash

work_dir="$PWD"
source "$work_dir/tools/ethics-env.sh"

cd app

if [ ! -d "target" ]; then
	echo "Run tools/build.sh first before running the app"
	exit 1
fi

cd target

jar_file=$(ls *.jar)

if [ -z "$jar_file" ]; then
	echo "Cannot find the installed app to run. Run tools/build.sh"
	exit 1
fi

echo "Starting $work_dir/app/target/$jar_file"

jvm_args="$@"

java $jvm_args -Dspring.profiles.default=prod -jar "$jar_file"

