#!/bin/sh

mvn -Dflamingo.uploader.xml=file:`pwd`/target/classes/rain.xml package exec:java