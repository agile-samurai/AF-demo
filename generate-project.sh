#!/bin/bash

ARTIFACT_ID=${1:-microservice} 
mvn archetype:generate -DarchetypeCatalog=local \
    -DarchetypeGroupId=group.u.mdas -DarchetypeArtifactId=microservice-archetype -DarchetypeVersion=1.0-SNAPSHOT \
    -DartifactId=${ARTIFACT_ID} -DgroupId=group.u.mdas -Dversion=1.0-SNAPSHOT -DinteractiveMode=false
