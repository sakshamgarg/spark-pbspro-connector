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

import org.apache.spark.SparkContext
import org.apache.spark.scheduler.{ExternalClusterManager, SchedulerBackend, TaskScheduler, TaskSchedulerImpl}
import org.apache.spark.internal.Logging

/**
 * Cluster Manager responsible for handling PBSPro Scheduler.
 *
 * See also: org/apache/spark/scheduler/ExternalClusterManager.scala in Spark core code.
 */
private[spark] class PbsClusterManager extends ExternalClusterManager with Logging {

  /**
   * Check if we can create scheduler components for the given URL
   *
   * @param master the string passed with the --master option
   * @return if PBS can handle this master URL
   */
  override def canCreate(master: String): Boolean = {
    master == "pbs"
  }

  /**
   * Create Task Scheduler according to the given SparkContext
   *
   * @param sparkContext the Spark application
   * @return task scheduler for the application
   */
  override def createTaskScheduler(sparkContext: SparkContext, masterURL: String): TaskScheduler = {
    logDebug("Creating new TaskScheduler")
    new TaskSchedulerImpl(sparkContext)
  }

  /**
   * Create a Scheduler Backend for the given SparkContext
   *
   * @param sparkContext the Spark application
   * @param master the string passed with the --master option
   * @param scheduler the task scheduler for the application
   */
  override def createSchedulerBackend(sparkContext: SparkContext,
      masterURL: String,
      scheduler: TaskScheduler): SchedulerBackend = {
    logDebug("Creating new SchedulerBackend")
    new PbsCoarseGrainedSchedulerBackend(scheduler, sparkContext, masterURL)
  }

  /**
   * Initialize the Task Scheduler and Scheduler Backend (after they are created).
   *
   * @param scheduler the task scheduler for the application
   * @param backend the scheduler backend to initialize
   */
  override def initialize(scheduler: TaskScheduler, backend: SchedulerBackend): Unit = {
    logDebug("Initializing Cluster Manager")
    scheduler.asInstanceOf[TaskSchedulerImpl].initialize(backend)
  }
}
