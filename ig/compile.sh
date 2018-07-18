#!/bin/bash
javac -classpath ext/commons-io-2.6.jar  -sourcepath ./ infoasys/cli/pangenes/Pangenes.java & jar cvf ig.jar infoasys/
