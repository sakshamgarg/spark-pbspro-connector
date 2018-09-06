package org.apache.spark.scheduler.cluster.pbs

import java.io.File

import scala.sys.process._

import org.apache.spark.SparkContext
import org.apache.spark.executor.PbsExecutorInfo

private[pbs] object PbsSchedulerUtils {

  /**
   * Start an Executor.
   *
   * @param sparkContext spark application
   * @return Executor info for the started executor
   */
  private def newExecutor(sparkContext: SparkContext): Option[PbsExecutorInfo] = {
    val sparkHome = sparkContext.getSparkHome().toString()
    val runScript = new File("./bin/spark-class").getPath
    val environ = ""
    val opts = "--driver-url ubuntu:33487" +
        "--hostname ubuntu" +
        "--cores 2" +
        "--app-id 123" +
        "--worker-url ubuntu:13245" +
        "--user-class-path file://" +
        "--executor-id 1234"

    val command = "%s \"%s\" org.apache.spark.executor.PbsCoarseGrainedExecutorBackend %s"
      .format(environ, runScript, opts)

    val cmd = "qsub -- %s".format(command)
    val out = cmd.!!
    println(out)
    None
  }

  /**
   * Start Executors such that there is a specific number of total executors running.
   *
   * @param sparkContext spark application
   * @param totalExecutors total number of executors wanted (including already running)
   */
  def startExecutors(sparkContext: SparkContext, totalExecutors: Int): Boolean = {
    // FIXME: Check for already running executors
    for (i <- 1 to totalExecutors) {
      newExecutor(sparkContext) match {
        case Some(executor: PbsExecutorInfo) =>
        case None =>
      }
    }
    true
  }

}
