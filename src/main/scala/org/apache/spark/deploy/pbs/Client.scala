package org.apache.spark.deploy.pbs

import java.io.File

import org.apache.spark.{SparkConf, SparkException}
import org.apache.spark.internal.Logging

import org.apache.spark.pbs.Utils

private[pbs] class Client(args: ClientArguments, sparkConf: SparkConf) extends Logging {

  private val defaultDriverCores: Int = 1
  private val defaultDriverMemory: String = "1G"
  private val defaultAppName: String = "spark-driver"

  /**
   * This is responsible to build and start a driver program on a node in the cluster. This driver
   * then builds the SparkContext and starts the application to which other Executors connect.
   *
   * Our objective is to get the spark-submit to a node in the cluster. This can be achieved if we
   * simply qsub the spark-submit command, but in client mode so that the driver is started on
   * whatever node the qsub went for execution. The driver which will be started will handle the
   * launching of executors on its own.
   */
  def run() {
    val sparkHome: String = sparkConf.getOption("spark.executor.pbs.home")
      .orElse(sparkConf.getOption("spark.home"))
      .getOrElse {
        throw new SparkException("Executor Spark home `spark.executor.pbs.home` not set!")
      }
    val appName: String = sparkConf.getOption("spark.app.name")
      .getOrElse(defaultAppName)
      .replaceAll("\\s", "")
    val driverCores: Int = sparkConf.getOption("spark.driver.cores") match {
      case Some(cores) => cores.toInt
      case None => defaultDriverCores
    }
    val driverMemory: String = sparkConf.getOption("spark.driver.memory")
      .getOrElse(defaultDriverMemory)
    val runScript: String = new File(sparkHome, "bin/spark-submit").getPath

    val opts: String = "--master pbs --deploy-mode client" +
        s" --driver-cores $driverCores --driver-memory $driverMemory" +
        s" --class ${args.mainClass} ${args.primaryJavaResource} ${args.arg}"
        s"" // TODO: Configure the driver command for python, R etc.

    val driverCommand = s"$runScript $opts"

    logInfo(s"Starting driver: $driverCommand")
    Utils.qsub(appName, driverCores, driverMemory, driverCommand)
  }
}
