package org.apache.spark.scheduler.cluster.pbs

import scala.concurrent.Future

import org.apache.spark.{SecurityManager, SparkConf, SparkContext}
import org.apache.spark.scheduler.{TaskScheduler, TaskSchedulerImpl}
import org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend

import org.pbspro.pbs.Offer

private[spark] class PbsCoarseGrainedSchedulerBackend(
    scheduler: TaskScheduler,
    sc: SparkContext,
    masterURL: String)
  extends CoarseGrainedSchedulerBackend(scheduler.asInstanceOf[TaskSchedulerImpl], sc.env.rpcEnv) {

  private val driver = new PbsSchedulerDriver()

  /**
   * Start and initialize a scheduler driver.
   */
  override def start() {
    logInfo("Starting PBS Scheduler backend")
    super.start()
    driver.init()
  }

  /**
   * Stop the scheduler driver.
   */
  override def stop() {
    logInfo("Stopping PBS Scheduler backend")
  }

  /**
   * Kill the executors in the given list.
   *
   * @param executorIds list of Executor IDs to kill
   * @return if all the Executors were killed
   */
  override def doKillExecutors(executorIds: Seq[String]): Future[Boolean] = Future.successful {
    for (executor <- executorIds) {
      driver.killExecutor(executor)
    }
    true
  }

  /**
   * Notify the scheduler about the number of Executors wanted by the application.
   *
   * @param requestedTotal number of Executors wanted (including already allocated)
   * @return if the request is acknowledged
   */
  override def doRequestTotalExecutors(requestedTotal: Int): Future[Boolean] = Future.successful {
    logInfo(requestedTotal + " executors requested")
    for (i <- 0 to requestedTotal) {
      driver.createExecutor()
    }
    true
  }

}
