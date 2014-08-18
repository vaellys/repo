/usr/bin/env sh

CP=.:dependency/*
OPT=
nohup java -cp $CP $OPT org.mas.codehaus.finder.serverStart.JServer 
