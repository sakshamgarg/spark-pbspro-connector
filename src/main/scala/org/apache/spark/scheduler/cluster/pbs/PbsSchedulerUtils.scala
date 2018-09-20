package org.apache.spark.scheduler.cluster.pbs

import java.io.File
import scala.collection.mutable.ListBuffer

import org.apache.spark.{SparkContext, SparkException}
import org.apache.spark.rpc.RpcEndpointAddress
import org.apache.spark.internal.Logging
import org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend

import org.apache.spark.pbs.Utils
import org.apache.spark.executor.pbs.PbsExecutorInfo

private[pbs] object PbsSchedulerUtils extends Logging {

  private var last_task = 0
  private val defaultAppId: String = "app"
  private val defaultExecutorCores: Int = 2
  private val defaultExecutorMemory: String = "1G"

  /**
   * Start an Executor
   *
   * Parse the SparkContext to extract params necessary for the executor. With these params, submit
   * PbsExecutor as a PBS job via qsub.
   *
   * @param sparkContext spark application
   * @return Executor info for the executor submitted. The executor may or may not be running.
   */
  private def newExecutor(sparkContext: SparkContext): Option[PbsExecutorInfo] = {
    val driverUrl = RpcEndpointAddress(
      sparkContext.conf.get("spark.driver.host"),
      sparkContext.conf.get("spark.driver.port").toInt,
      CoarseGrainedSchedulerBackend.ENDPOINT_NAME).toString

    val environ: String = ""  // TODO
    val taskId: Int = last_task
    last_task += 1
    val appId: String = sparkContext.conf.getOption("spark.app.name")
      .getOrElse(defaultAppId).replaceAll("\\s", "")
    val numCores: Int = sparkContext.conf.getOption("spark.executor.cores") match {
      case Some(cores) => cores.toInt
      case None => defaultExecutorCores
    }
    val memory: String = sparkContext.conf.getOption("spark.driver.memory")
      .getOrElse(defaultExecutorMemory)
    val sparkHome: String = sparkContext.conf.getOption("spark.executor.pbs.home")
      .orElse(sparkContext.getSparkHome())
      .getOrElse {
        throw new SparkException("Executor Spark home `spark.executor.pbs.home` not set!")
      }

    val runScript = new File(sparkHome, "/bin/spark-class").getPath
    val opts = s"--driver-url $driverUrl" +
        s" --cores $numCores" +
        s" --app-id $appId" +
        s" --executor-id $taskId"
    val command = s"$runScript org.apache.spark.executor.pbs.PbsExecutor $opts"
    val jobName = s"spark-$appId-$taskId"

    val jobId = Utils.qsub(jobName, numCores, memory, command)
    Some(new PbsExecutorInfo(jobId, jobName, numCores, memory))
  }

  /**
   * Start Executors such that there is a specific number of total executors running.
   *
   * @param sparkContext spark application
   * @param totalExecutors total number of executors wanted (including already running)
   * @param executors list of all executors (including those which may be in queued state)
   */
  def startExecutors(
    sparkContext: SparkContext,
    totalExecutors: Int,
    executors: ListBuffer[PbsExecutorInfo]): Boolean = {
    // FIXME: Check for already running executors
    for (i <- 1 to totalExecutors) {
      newExecutor(sparkContext) match {
        case Some(executor: PbsExecutorInfo) =>
          logInfo(s"qsub: ${executor.jobId} : ${executor.jobName}")
          executors.append(executor)
        case None =>
          throw new SparkException("Failed to try starting executor")
      }
    }
    true
  }

}
