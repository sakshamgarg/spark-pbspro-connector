package org.apache.spark.deploy.pbs

import org.apache.spark.pbs.Utils
import org.apache.spark.executor.pbs.PbsExecutorInfo

private[pbs] class PbsApplicationInfo(val driverJobId: String) extends PbsJobInfo(driverJobId) {
  var executors: Array[PbsExecutorInfo] = _

  override def isRunning: Boolean = { state == "R" }

  init()

  private def init() {
    executors = getExecutors()
    state = "Q"
    for (executor <- executors) {
      cores += executor.cores
      state = "R"
    }
  }

  private def getExecutors(): Array[PbsExecutorInfo] = {
    new Array[PbsExecutorInfo](0)
  }
}
