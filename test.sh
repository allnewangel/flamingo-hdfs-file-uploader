#!/bin/sh

mvn -Duploader.job.xml=file:`pwd`/target/classes/rain.xml clean package exec:java