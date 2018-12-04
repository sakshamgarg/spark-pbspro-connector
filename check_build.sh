#!/bin/sh

spark_repo="https://github.com/apache/spark"
pbs_repo="https://github.com/UtkarshMe/Spark-PBSPro"

quit() {
  echo "$1"
  exit 1
}


# clone spark
cd /tmp
git clone $spark_repo                       || quit "Unable to clone spark repo"
cd spark

# clone pbs
git clone $pbs_repo resource-managers/pbs   || quit "Unable to clone pbs repo"

# apply patches
git am resource-managers/pbs/*.patch        || quit "Unable to apply patch to spark"

# build
build/mvn -T 4 -q -DskipTests -Ppbs package || quit "Unable to build spark with pbs"
