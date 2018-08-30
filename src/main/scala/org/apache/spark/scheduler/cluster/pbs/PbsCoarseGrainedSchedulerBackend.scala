package org.apache.spark.scheduler.cluster.pbs

import scala.concurrent.Future

import org.apache.spark.{SecurityManager, SparkConf, SparkContext}
import org.apache.spark.scheduler.{TaskScheduler, TaskSchedulerImpl}
import org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend

private[spark] class PbsCoarseGrainedSchedulerBackend(
    scheduler: TaskScheduler,
    sc: SparkContext,
    masterURL: String)
  extends CoarseGrainedSchedulerBackend(scheduler.asInstanceOf[TaskSchedulerImpl], sc.env.rpcEnv) {

  val driver = new PbsSchedulerDriver()

  /**
   * Create and start a scheduler driver
   */
  override def start() {
    super.start()
    driver.start()
  }

  /**
   * Stop the scheduler driver
   */
  override def stop() {
    // TODO
  }

}
