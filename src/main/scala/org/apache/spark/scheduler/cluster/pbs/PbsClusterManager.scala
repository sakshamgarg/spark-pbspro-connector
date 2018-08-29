package org.apache.spark.scheduler.cluster.pbs

import org.apache.spark.SparkContext
import org.apache.spark.scheduler.{ExternalClusterManager, SchedulerBackend, TaskScheduler}

private[spark] class PbsClusterManager extends ExternalClusterManager {

  override def canCreate(masterURL: String): Boolean = {
    // TODO
    true
  }

  override def createTaskScheduler(sc: SparkContext, masterURL: String): TaskScheduler = {
    // TODO
    null
  }

  override def createSchedulerBackend(sc: SparkContext,
      masterURL: String,
      scheduler: TaskScheduler): SchedulerBackend = {
    // TODO
    null
  }

  override def initialize(scheduler: TaskScheduler, backend: SchedulerBackend): Unit = {
    // TODO
    null
  }
}
