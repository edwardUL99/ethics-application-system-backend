#! /usr/bin/env bash

arg="$1"

if [ "$arg" != "start" ] && [ "$arg" != "stop" ] && [ "$arg" != "restart" ] && [ "$arg" != "status" ]; then
	echo "Usage: ./clamav-daemon.sh start|stop|restart|status"
	exit 1
fi

systemctl "$arg" clamav-daemon
systemctl "$arg" clamav-freshclam
