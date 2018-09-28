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

import scala.concurrent.Future

import org.apache.spark.{SecurityManager, SparkConf, SparkContext}
import org.apache.spark.internal.Logging
import org.apache.spark.executor.pbs.PbsExecutorInfo
import org.apache.spark.scheduler.{TaskScheduler, TaskSchedulerImpl}
import org.apache.spark.scheduler.cluster.{CoarseGrainedSchedulerBackend, SchedulerBackendUtils}

private[spark] class PbsCoarseGrainedSchedulerBackend(
    scheduler: TaskScheduler,
    sparkContext: SparkContext,
    masterURL: String)
  extends CoarseGrainedSchedulerBackend(
    scheduler.asInstanceOf[TaskSchedulerImpl],
    sparkContext.env.rpcEnv) with Logging {

  private val initialExecutors: Int = SchedulerBackendUtils.getInitialTargetExecutorNumber(conf)
  private val defaultMinRegisteredRatio: Double = 0.8

  protected override val minRegisteredRatio: Double = {
    if (conf.getOption("spark.scheduler.minRegisteredResourcesRatio").isEmpty) {
      defaultMinRegisteredRatio
    } else {
      super.minRegisteredRatio
    }
  }

  /**
   * Start the scheduler and initialize dependent components.
   */
  override def start() {
    logInfo("Starting PBS Scheduler backend")
    super.start()
    logInfo("Starting initial " + initialExecutors + " executors")
    PbsSchedulerUtils.startExecutors(sparkContext, initialExecutors)
  }

  /**
   * Stop the scheduler driver.
   */
  override def stop() {
    logInfo("Stopping PBS Scheduler backend")
  }

  /**
   * Check if we have enough for our need (not for want... we can never have enough for our want).
   *
   * @return true if enough resources registered
   */
  override def sufficientResourcesRegistered(): Boolean = {
    totalRegisteredExecutors.get() >= initialExecutors * minRegisteredRatio
  }

  /**
   * Kill the executors in the given list.
   *
   * @param executorIds list of Executor IDs to kill
   * @return if all the Executors were killed
   */
  override def doKillExecutors(executorIds: Seq[String]): Future[Boolean] = Future.successful {
    for (executor <- executorIds) {
      logInfo("Killing executor: " + executor)
      // executorDriver.kill(executor)
    }
    true
  }

  /**
   * Notify the scheduler about the number of Executors wanted by the application. Start up
   * Executors (ExecutorBackend) on PBS, which will in turn register themselves with the
   * SchedulerDriver.
   *
   * TODO: Change it to use threads to launch executors
   *
   * @param requestedTotal number of Executors wanted (including already allocated)
   * @return if the request is acknowledged
   */
  override def doRequestTotalExecutors(requestedTotal: Int): Future[Boolean] = Future.successful {
    logInfo(requestedTotal + " executors requested")
    PbsSchedulerUtils.startExecutors(sparkContext, requestedTotal)
  }

}
