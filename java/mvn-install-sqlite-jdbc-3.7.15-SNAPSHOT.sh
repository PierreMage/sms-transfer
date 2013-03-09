#!/bin/sh
wget https://bitbucket.org/xerial/sqlite-jdbc/downloads/sqlite-jdbc-3.7.15-SNAPSHOT.jar
mvn install:install-file -Dfile=sqlite-jdbc-3.7.15-SNAPSHOT.jar -DgroupId=org.xerial -DartifactId=sqlite-jdbc -Dversion=3.7.15-SNAPSHOT -Dpackaging=jar
rm -f sqlite-jdbc-3.7.15-SNAPSHOT.jar