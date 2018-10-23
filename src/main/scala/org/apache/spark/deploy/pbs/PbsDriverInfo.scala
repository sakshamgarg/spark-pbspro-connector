package org.apache.spark.deploy.pbs

import scala.util.matching.Regex
import org.apache.spark.pbs.Utils

private[pbs] case class PbsDriverInfo(id: String) extends PbsJobInfo(id) {
  var mainClass: String = _
  var className: String = _

}

private[pbs] object PbsDriverInfo {
  val SPARK_JOB_REGEX: Regex  = """([0-9]+\.[a-z]+) +(sparkjob-[a-zA-Z\-\.]*.*)"""r

  def getJobId(jobString: String): Option[String] = {
    jobString match {
      case SPARK_JOB_REGEX(job, _) =>
        Some(job)
      case _ =>
        None
    }
  }
}
