package org.apache.spark.scheduler.cluster.pbs

import org.pbspro.pbs.PBS

private[pbs] class PbsSchedulerDriver() {

  /**
   * Register with pbs and initialize stuff.
   */
  def init() {
  }

  /**
   * Submit a qsub which will act as an Executor.
   *
   * @return The Executor ID
   */
  def createExecutor(): String = {
    null
  }

  /**
   * Kill the Executor job.
   *
   * @param id ID of the executor
   * @return if the Executor was killed
   */
  def killExecutor(id: String): Boolean = {
    true
  }
}
