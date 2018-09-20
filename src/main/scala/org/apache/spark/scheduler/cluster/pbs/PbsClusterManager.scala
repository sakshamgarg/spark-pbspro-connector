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
