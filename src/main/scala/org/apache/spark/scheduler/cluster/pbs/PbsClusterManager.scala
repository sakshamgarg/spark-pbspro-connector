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
   */
  override def canCreate(masterURL: String): Boolean = {
    /* checking if the URL starts with "pbs" e.g. "pbs://host:port" */
    masterURL.startsWith("pbs")
  }

  /**
   * Create Task Scheduler according to the given SparkContext
   */
  override def createTaskScheduler(sc: SparkContext, masterURL: String): TaskScheduler = {
    logDebug("Creating new TaskScheduler")
    new TaskSchedulerImpl(sc)
  }

  /**
   * Create a Scheduler Backend for the given SparkContext
   */
  override def createSchedulerBackend(sc: SparkContext,
      masterURL: String,
      scheduler: TaskScheduler): SchedulerBackend = {
    // TODO: Check and allow for a fine grained scheduler if needed.
    logDebug("Creating new SchedulerBackend")
    new PbsCoarseGrainedSchedulerBackend(scheduler, sc, masterURL)
  }

  /**
   * Initialize the Task Scheduler and Scheduler Backend (after they are created).
   */
  override def initialize(scheduler: TaskScheduler, backend: SchedulerBackend): Unit = {
    logDebug("Initializing Cluster Manager")
    scheduler.asInstanceOf[TaskSchedulerImpl].initialize(backend)
  }
}
