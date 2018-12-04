package org.apache.spark.deploy.pbs

import org.apache.spark.pbs.Utils

private[pbs] class PbsApplicationInfo(
    val driver: PbsDriverInfo,
    val executors: List[PbsExecutorInfo])
  extends PbsJobInfo(driver.jobId) {

  override def isRunning: Boolean = { state == "R" }

  init()

  private def init() {
    state = "Q"
    executors.foreach(executor => {
      println("exec: " + executor.jobId)
      cores += executor.cores
      state = "R"
    })
  }
}
