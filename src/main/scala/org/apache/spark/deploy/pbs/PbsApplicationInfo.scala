package org.apache.spark.deploy.pbs

import org.apache.spark.pbs.Utils

import org.apache.spark.executor.pbs.PbsExecutorInfo

private[pbs] class PbsApplicationInfo(val driverJobId: String) {
  var id: String = _
  var name: String = _
  var cores: Int = _
  var mem: String = _
  var submissionDate: String = _
  var state: String = _
  var user: String = _

  var driver: PbsDriverInfo = PbsDriverInfo(driverJobId)
  var executors: Array[PbsExecutorInfo] = getExecutors(driver)

  init()

  val stateString: String = {
    state match {
      case "R" =>
        "RUNNING"
      case "Q" =>
        "SUBMITTED"
      case "C" =>
        "COMPLETED"
      case _ =>
        "UNKNOWN"
    }
  }

  private def init() {
    id = driver.jobId
    name = driver.name

    // TODO: Cores, mem should be the sum of executors too.
    cores = driver.cores
    mem = driver.mem

    state = "Q"
    user = driver.user

    for (executor <- executors) {
      cores += executor.cores
      state = "R"
    }
  }

  private def getExecutors(driver: PbsDriverInfo): Array[PbsExecutorInfo] = {
    new Array[PbsExecutorInfo](0)
  }
}
