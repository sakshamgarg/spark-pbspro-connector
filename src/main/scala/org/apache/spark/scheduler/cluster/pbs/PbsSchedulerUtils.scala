/*
 * Copyright (C) 1994-2019 Altair Engineering, Inc.
 * For more information, contact Altair at www.altair.com.
 *
 * This file is part of the PBS Professional ("PBS Pro") software.
 *
 * Open Source License Information:
 *
 * PBS Pro is free software. You can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * PBS Pro is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Commercial License Information:
 *
 * For a copy of the commercial license terms and conditions,
 * go to: (http://www.pbspro.com/UserArea/agreement.html)
 * or contact the Altair Legal Department.
 *
 * Altair’s dual-license business model allows companies, individuals, and
 * organizations to create proprietary derivative works of PBS Pro and
 * distribute them - whether embedded or bundled with other software -
 * under a commercial license agreement.
 *
 * Use of Altair’s trademarks, including but not limited to "PBS™",
 * "PBS Professional®", and "PBS Pro™" and Altair’s logos is subject to Altair's
 * trademark licensing policies.
 *
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
    val sparkHome: String = sparkContext.conf.getOption("spark.pbs.executor.home")
      .orElse(sparkContext.getSparkHome())
      .getOrElse {
        throw new SparkException("Executor Spark home `spark.pbs.executor.home` not set!")
      }

    val runScript = new File(sparkHome, "/bin/spark-class").getPath
    val opts = s"--driver-url $driverUrl" +
        s" --cores $numCores" +
        s" --app-id $appId" +
        s" --executor-id $taskId"
    val command = s"$runScript org.apache.spark.executor.pbs.PbsExecutor $opts"
    val jobName = s"sparkexec-$appId-$taskId"

    val jobId = Utils.qsub(jobName, numCores, memory, command)
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
