package org.apache.spark.deploy.pbs

import org.apache.spark.deploy.SparkApplication
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging

private[spark] class PbsClusterApplication extends SparkApplication with Logging {

  /**
   * Create and run a new spark application on a node in the cluster.
   *
   * @param args arguments from SparkSubmit
   * @param conf spark configuration
   */
  override def start(args: Array[String], conf: SparkConf): Unit = {
    logInfo("Starting new client with master URL: " + conf.get("spark.master"))
    new Client(new ClientArguments(args), conf).run()
  }
}
