#! /usr/bin/env bash

tools/build.sh -DskipTests
tools/run.sh -Daccount.always.confirm=true -Dspring.profiles.active=embdedded -Dantivirus.disable=true -Demail.disable=true