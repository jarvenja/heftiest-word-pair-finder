#!/bin/sh
SRC_PATH="./src/main/java"
javac -classpath "${SRC_PATH}" -d ./bin "${SRC_PATH}/pro/jarvenpaa/wundernuts/App.java"
