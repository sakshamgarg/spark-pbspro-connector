package org.apache.spark.executor.pbs

import org.apache.spark.pbs.Utils

/**
 * Class to store info about the executor which was submitted as a PBS job. The job may or may not
 * be in running state at a given time.
 *
 * @param jobId The ID of the job returned from qsub
 * @param jobName The name that was set for the job
 * @param cores Number of cores allocated to the executor
 * @param memory Memory allocated to the executor
 */
private[spark] case class PbsExecutorInfo(
  jobId: String,
  jobName: String,
  cores: Int,
  memory: String) {

    /**
     * Check if the job is in running state. This does not mean that the executor has registered
     * with the driver.
     *
     * @return if the job is running
     */
    def isRunning: Boolean = {
      val stat = Utils.qstat(jobId, "-f")
      Utils.grep(stat, "job_status = R").nonEmpty
    }
}
