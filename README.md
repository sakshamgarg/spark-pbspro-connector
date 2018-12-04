# Spark PBSPro Support
This adds support for PBS Professional HPC resource manager to Apache Spark

## How to:
In the spark project root, punch in these commands:
```bash
# Clone the repo
git clone https://github.com/UtkarshMe/Spark-PBSPro resource-managers/pbs

# Apply patches from the `patches` folder to spark (in the root directory).
git am resource-managers/pbs/patches/*

# Build!
build/mvn -DskipTests -Ppbs package
```

You can run Spark on the PBS cluster as follows:
```bash
# start spark shell. only in client mode
./bin/spark-shell --master pbs

# submit a spark application in client mode
./bin/spark-submit --master pbs --deploy-mode client --class org.apache.spark.examples.SparkPi $SPARK_HOME/examples/target/scala-2.11/jars/spark-examples_2.11-2.4.0-SNAPSHOT.jar 100

# submit a spark application in cluster mode
./bin/spark-submit --master pbs --deploy-mode cluster --class org.apache.spark.examples.SparkPi $SPARK_HOME/examples/target/scala-2.11/jars/spark-examples_2.11-2.4.0-SNAPSHOT.jar 100
```
