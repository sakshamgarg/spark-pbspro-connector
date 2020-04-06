#!/bin/sh -x

spark_repo="https://github.com/apache/spark"

quit() {
  echo "$1"
  exit 1
}

if [ -z "$TRAVIS_BUILD_DIR" ]; then
	pbs_patch_dir=$(pwd)
else
	pbs_patch_dir=$TRAVIS_BUILD_DIR
fi

# clone spark
cd /tmp
git clone $spark_repo                       || quit "Unable to clone spark repo"
cd spark

# copy pbs_patch_dir
mv $pbs_patch_dir resource-managers/pbs     || quit "Unable to copy pbs_patch repo"

# apply patches
git am resource-managers/pbs/*.patch        || quit "Unable to apply patch to spark"

# build
export MAVEN_SKIP_RC=1
build/mvn -q -DskipTests -Ppbs clean package      || quit "Unable to build spark with pbs"
