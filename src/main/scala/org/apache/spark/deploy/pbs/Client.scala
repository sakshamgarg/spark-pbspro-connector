package org.apache.spark.deploy.pbs

import java.io.File
import scala.sys.process._

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging

private[pbs] class Client(args: ClientArguments, sparkConf: SparkConf) extends Logging {

  /**
   * This is responsible to build and start a driver program on a node in the cluster. This driver
   * then builds the SparkContext and starts the application to which other Executors connect.
   */
  def run() {

    val appName = sparkConf.getOption("spark.app.name").getOrElse("spark-driver")
    val appId = appName                     // TODO

    logInfo("Building driver program")
    val driverCommand = "/bin/sleep 10"     // TODO

    logInfo("Starting driver.")
    val cmd = s"qsub -N $appName -- $driverCommand"
    val out = cmd.!!
    println(out)

  }
}
