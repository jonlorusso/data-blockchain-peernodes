#!/bin/sh

# Before running, need to have these packages installed:
# - maven
# - openjdk-8-jdk-headless

JAVA_OPTS="-Xmx1024"
export JAVA_OPTS

# FIXME need credentials for git clones

START_DIR="$PWD"
SWATT_COMMON_LIBRARY="swatt-common-library"
BLOCKCHAIN_JAVA="blockchain-java"

# clone and install swatt common library
if [ ! -d "$SWATT_COMMON_LIBRARY" ]; then
    git clone https://bitbucket.dev.ruvpfs.swatt.exchange:9443/b/scm/dev/swatt-common-library.git
fi

cd "$SWATT_COMMON_LIBRARY"
git fetch origin
git checkout origin/master

mvn install

# Return to start dir
cd "$START_DIR"

# clone and run blockchain-java
if [ ! -d "$BLOCKCHAIN_JAVA" ]; then
    git clone https://bitbucket.dev.ruvpfs.swatt.exchange:9443/b/scm/api/blockchain-java.git
fi

cd "$BLOCKCHAIN_JAVA"
git fetch origin
git checkout origin/develop

mvn package
mvn exec:java