#!/usr/bin/env sh

dist="mas-0.0.1"

if [ ! -e $dist ];then
	mkdir $dist
fi
if [ ! -e $dist/conf ];then
	mkdir $dist/conf
fi
if [ ! -e $dist/scripts ];then
	mkdir $dist/scripts
fi
mvn dependency:copy-dependencies
mvn -DskipTests package


cp target/mas-0.0.1.jar $dist/
cd $dist
unzip  mas-0.0.1.jar
find . -maxdepth 1 -type f -exec mv -f {} conf/ \;
find conf -iname *.py -exec mv -f {} scripts/ \;
mv  conf/mas-0.0.1.jar ./
rm -rf com
rm -rf cn
rm -rf META-INF
cp -ru ../target/dependency .
zip -d mas-0.0.1.jar *.conf *.py *.properties *.xml *.pyc *.cfg
