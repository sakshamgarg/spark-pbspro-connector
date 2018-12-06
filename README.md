# Spark PBSPro Support
This adds support for PBS Professional HPC resource manager to Apache Spark

Status of build with latest Spark: [![Build Status](https://travis-ci.org/UtkarshMe/Spark-PBSPro.svg?branch=master)](https://travis-ci.org/UtkarshMe/Spark-PBSPro)

## How to:
In the spark project root, punch in these commands:
```bash
# Clone the repo
git clone https://github.com/UtkarshMe/Spark-PBSPro resource-managers/pbs

# Apply patch to spark (in the root directory).
git am resource-managers/pbs/*.patch

# Build!
build/mvn -DskipTests -Ppbs package
```

Add executor home to your configuration:
```bash
# in file conf/spark-defaults.conf add line:
spark.pbs.executor.home "SPARK INSTALLATION DIRECTORY PATH IN PBS MOMS"
```

You can run Spark on the PBS cluster as follows:
```bash
# start spark shell. only in client mode
./bin/spark-shell --master pbs

# submit a spark application in client mode
./bin/spark-submit --master pbs --deploy-mode client --class org.apache.spark.examples.SparkPi $SPARK_HOME/examples/target/scala-2.12/jars/spark-examples_2.12-3.0.0-SNAPSHOT.jar 100

# submit a spark application in cluster mode
./bin/spark-submit --master pbs --deploy-mode cluster --class org.apache.spark.examples.SparkPi $SPARK_HOME/examples/target/scala-2.12/jars/spark-examples_2.12-3.0.0-SNAPSHOT.jar 100
```
