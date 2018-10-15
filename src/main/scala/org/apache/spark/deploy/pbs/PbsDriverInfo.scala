package org.apache.spark.deploy.pbs

import scala.util.matching.Regex
import org.apache.spark.pbs.Utils

private[pbs] case class PbsDriverInfo(jobId: String) {
  var jobName: String = null
  var user: String = null
  var submissionDate: String = null
  var ncpus: String = null
  var mem: String = null
  var state: String = null

  val ROW_REGEX: Regex = """(.*) = (.*)"""r

  def init() {
    Utils.qstat(jobId, "-f").split("\n").foreach(jobRow => {
      try {
        val ROW_REGEX(key, value) = jobRow
        key.trim() match {
          case "Job_Name" =>
            jobName = value.trim()
          case "Job_Owner" =>
            user = value.trim()
          case "resources_used.ncpus" =>
            ncpus = value
          case "resources_used.mem" =>
            mem = value
          case "qtime" =>
            submissionDate = value
          case "job_state" =>
            state = value
          case _ =>  // ignored (for now)
        }
      } catch {
        case e: scala.MatchError => // TODO: Split lines end up here. Fix them.
      }
    })
  }
}

private[pbs] object PbsDriverInfo {
  val SPARK_JOB_REGEX: Regex  = """([0-9]+\.[a-z]+) +(org[a-zA-Z\-\.]*.*)"""r

  def create(jobString: String): PbsDriverInfo = {
    jobString match {
      case SPARK_JOB_REGEX(job, _) =>  // job is a spark job
        val driver = PbsDriverInfo(job)
        driver.init()
        driver
      case _ =>
        null
    }
  }
}
