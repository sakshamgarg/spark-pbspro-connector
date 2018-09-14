package org.apache.spark.deploy.pbs

import java.io.File
import scala.sys.process._

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.rpc.RpcEndpointAddress
import org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend

private[pbs] class Client(args: ClientArguments, sparkConf: SparkConf) extends Logging {

  /**
   * This is responsible to build and start a driver program on a node in the cluster. This driver
   * then builds the SparkContext and starts the application to which other Executors connect.
   *
   * Our object is to get the spark-submit to a node in the cluster. This can be achieved if we
   * simply qsub the spark-submit command, but in client mode so that the driver is started on
   * whatever node the qsub went for execution. The driver which will be started will handle the
   * launching of executors on its own.
   */
  def run() {

    val sparkHome = sparkConf.getOption("spark.home") match {
      case Some(home) =>
        home
      case None =>
        "/home/utkmah/code/spark/"  // FIXME
    }

    val appName = sparkConf.getOption("spark.app.name").getOrElse("spark-driver")
    val appId = appName  // TODO

    val driverMemory = sparkConf.getOption("spark.driver.memory").getOrElse("1g")
    val driverCores = sparkConf.getOption("spark.driver.cores").getOrElse("1")
    val runScript = new File(sparkHome, "bin/spark-submit").getPath

    // TODO: Configure the driver command for python, R etc.
    val driverCommand = s"$runScript --master pbs --deploy-mode client " +
        s"--driver-cores $driverCores --driver-memory $driverCores " +
        s"--class ${args.mainClass} ${args.primaryJavaResource} ${args.arg}"
    val cmd = s"qsub -N $appName -l select=1:ncpus=$driverCores:mem=$driverMemory -- $driverCommand"

    logInfo(s"Starting driver: $cmd")
    val out = cmd.!!

  }
}
