#! /usr/bin/env bash

systemctl stop clamav-freshclam
systemctl stop clamav-daemon
freshclam 
systemctl start clamav-daemon
systemctl start clamav-freshclam
