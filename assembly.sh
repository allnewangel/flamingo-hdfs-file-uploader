#!/bin/sh

mvn -Dmaven.test.skip=true clean package dependency:copy-dependencies assembly:assembly