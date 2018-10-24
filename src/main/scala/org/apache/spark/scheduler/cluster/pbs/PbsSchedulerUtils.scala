/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.scheduler.cluster.pbs

import java.io.File

import org.apache.spark.{SparkContext, SparkException}
import org.apache.spark.rpc.RpcEndpointAddress
import org.apache.spark.internal.Logging
import org.apache.spark.executor.pbs.PbsExecutorInfo
import org.apache.spark.pbs.Utils
import org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend

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

    var environ: String = ""  // TODO
    val taskId: Int = last_task
    last_task += 1
    val appId: String = sparkContext.conf.getOption("spark.app.name")
      .getOrElse(defaultAppId).replaceAll("\\s", "")
    val numCores: Int = sparkContext.conf.getOption("spark.executor.cores") match {
      case Some(cores) => cores.toInt
      case None => defaultExecutorCores
    }
    val memory: String = sparkContext.conf.getOption("spark.executor.memory")
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
    val jobName = s"sparkexec-$appId-$taskId"

    sys.env.get("PBS_JOBID") match {
      case Some(id) =>
        environ += s"SPARK_DRIVER=$id"
      case None =>
    }

    val jobId = Utils.qsub(jobName, numCores, memory, command, environ)
    Some(new PbsExecutorInfo(jobId, jobName, numCores, memory))
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
          logInfo(s"qsub: ${executor.jobId} : ${executor.jobName}")
        case None =>
          throw new SparkException("Failed to try starting executor")
      }
    }
    true
  }

}
