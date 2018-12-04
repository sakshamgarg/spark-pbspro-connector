package org.apache.spark.deploy.pbs

private[pbs] case class PbsExecutorInfo(id: String) extends PbsJobInfo(id) {
  var driverId: String = _
}
