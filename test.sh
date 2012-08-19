#!/bin/sh

mvn -Dmaven.test.skip=true -Duploader.job.xml=file:`pwd`/target/test-classes/rain.xml clean package exec:java