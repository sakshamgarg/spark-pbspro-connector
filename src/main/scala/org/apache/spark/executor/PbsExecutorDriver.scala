package org.apache.spark.executor

import scala.sys.process._

private[spark] class PbsExecutorDriver() {

  /**
   * Start an Executor on PBS.
   *
   * @param executorId the id to assign to this Executor
   * @return the ExecutorInfo for the spawned Executor
   */
  def spawn(execId: String): Option[PbsExecutorInfo] = {
    val cmd = "qsub -- /bin/sleep 100"
    val out = cmd.!!
    println(out)
    None
  }

  /**
   * Kill the Executor on PBS.
   *
   * @param executorId the ID of the executor
   * @return if the executor was killed successfully
   */
  def kill(executorId: String): Boolean = {
    false
  }
}
