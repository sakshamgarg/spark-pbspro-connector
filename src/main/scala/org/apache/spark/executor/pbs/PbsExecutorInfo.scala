/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
