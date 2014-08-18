#!/usr/bin/env sh

host="$1"
rootDir=/home/uzoice/projects/mas/
dist=mas-0.0.1
rsync -auvR --progress $dist $host:$rootDir
