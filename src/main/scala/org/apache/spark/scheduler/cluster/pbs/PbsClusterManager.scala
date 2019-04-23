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
