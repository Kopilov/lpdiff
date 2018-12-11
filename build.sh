#!/bin/sh

#kotlinc-jvm -jdk-home $JAVA_HOME -include-runtime -d lpdiff.jar .
mvn compile
mvn exec:java
