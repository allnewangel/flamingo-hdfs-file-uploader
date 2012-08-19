#!/bin/sh

mvn -Duploader.job.xml=file:`pwd`/target/test-classes/rain.xml clean package exec:java