package org.apache.spark.deploy.pbs

import org.json4s.jackson.JsonMethods

import scala.util.matching.Regex
import org.apache.spark.pbs.Utils

private[pbs] case class PbsDriverInfo(jobId: String) {

  implicit val formats = org.json4s.DefaultFormats

  var jobName: String = null
  var name: String = null
  var user: String = null
  var submissionDate: String = null
  var ncpus: String = null
  var cores: Int = _
  var mem: String = null
  var state: String = null

  val NAME_REGEX: Regex = """.*\.(.+)"""r

  init()

  private def init() {
    val qstatData = JsonMethods.parse(
        Utils.qstat(jobId, "-f -F json").replaceAll("""\\\'""", """\'"""))
    (qstatData \ "Jobs").children.lift(0) match {
      case None =>
        return
      case Some(job) =>
        val resourceList = (job \ "Resource_List")
        jobName = (job \ "Job_Name").extract[String]
            .stripPrefix("sparkjob-") // TODO: Remove magic prefix
        user = (job \ "Job_Owner").extract[String]
        state = (job \ "job_state").extract[String]
        submissionDate = (job \ "qtime").extract[String]
        ncpus = (resourceList \ "ncpus").extract[String]
        mem = (resourceList \ "mem").extract[String]

        //val NAME_REGEX(className) = jobName
        //name = className
        name = jobName

        cores = ncpus.toInt
    }
  }

  val isRunning: Boolean = { state == "R" }
  val isQueued: Boolean = { state == "Q" }
  val isCompleted: Boolean = { state == "C" } // TODO: This is incorrect.
}

private[pbs] object PbsDriverInfo {
  val SPARK_JOB_REGEX: Regex  = """([0-9]+\.[a-z]+) +(sparkjob-[a-zA-Z\-\.]*.*)"""r

  def create(jobString: String): PbsDriverInfo = {
    /*jobString match {
      case SPARK_JOB_REGEX(job, _) =>  // job is a spark job
        PbsDriverInfo(job)
      case _ =>
        null
    }*/
    jobString match {
      case "" =>
        null
      case _ =>
        PbsDriverInfo(jobString)
    }
  }
}
