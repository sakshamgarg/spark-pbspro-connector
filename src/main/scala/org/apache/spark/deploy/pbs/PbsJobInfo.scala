package org.apache.spark.deploy.pbs

import org.json4s.jackson.JsonMethods

import org.apache.spark.SparkException
import org.apache.spark.pbs.Utils

private[pbs] class PbsJobInfo(val jobId: String) {

  // needed for json4s "extract" method
  implicit val formats = org.json4s.DefaultFormats

  var name: String = _
  var cores: Int = _
  var mem: String = _
  var state: String = _
  var submissionDate: String = _
  var user: String = _

  populate()

  def isRunning: Boolean = { state == "R" || state == "Q" }
  def isCompleted: Boolean = { state == "C" } // TODO: This is incorrect.

  def stateString: String = {
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

  protected def populate() {
    val data = JsonMethods.parse(Utils.qstat(jobId, "-f -F json").replaceAll("""\\\'""", """\'"""))
    (data \ "Jobs").children.lift(0) match {
      case None =>
        throw new SparkException(s"No job with id $jobId")

      case Some(job) =>
        name = (job \ "Job_Name").extract[String]
        user = (job \ "Job_Owner").extract[String]
        state = (job \ "job_state").extract[String]
        submissionDate = (job \ "qtime").extract[String]
        cores = (job \ "Resource_List" \ "ncpus").extract[Int]
        mem = (job \ "Resource_List" \ "mem").extract[String]
    }
  }
}

private[pbs] object PbsJobInfo {
  def getJobInfo(pbsJobRow: String): Option[PbsJobInfo] = {
    val Driver = """([0-9]+\.[a-z]+) +sparkjob-.*""".r 
    val Executor = """([0-9]+\.[a-z]+) +sparkexec-.*""".r 

    pbsJobRow match {
      case Driver(jobId) =>
        Some(new PbsDriverInfo(jobId))
      case Executor(jobId) =>
        Some(new PbsExecutorInfo(jobId))
      case _ =>
        None
    }
  }
}
