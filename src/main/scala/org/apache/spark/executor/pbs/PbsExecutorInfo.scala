/*
 * Copyright (C) 1994-2019 Altair Engineering, Inc.
 * For more information, contact Altair at www.altair.com.
 *
 * This file is part of the PBS Professional ("PBS Pro") software.
 *
 * Open Source License Information:
 *
 * PBS Pro is free software. You can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * PBS Pro is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Commercial License Information:
 *
 * For a copy of the commercial license terms and conditions,
 * go to: (http://www.pbspro.com/UserArea/agreement.html)
 * or contact the Altair Legal Department.
 *
 * Altair’s dual-license business model allows companies, individuals, and
 * organizations to create proprietary derivative works of PBS Pro and
 * distribute them - whether embedded or bundled with other software -
 * under a commercial license agreement.
 *
 * Use of Altair’s trademarks, including but not limited to "PBS™",
 * "PBS Professional®", and "PBS Pro™" and Altair’s logos is subject to Altair's
 * trademark licensing policies.
 *
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
