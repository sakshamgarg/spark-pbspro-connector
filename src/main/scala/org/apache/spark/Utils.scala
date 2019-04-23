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

package org.apache.spark.pbs

import scala.sys.process._

private[spark] object Utils {

  val prefix: String = s"/opt/pbs/bin"

  /**
   * Runs a shell command
   *
   * @param command the command to run with the argument
   * @return output of the command
   */
  def runCommand(command: String): String = {
    command.!!.trim()
  }

  /**
   * Submits a job to PBS
   *
   * @param name a name for the job
   * @param cores number of cores for the job
   * @param memory memory to allocate for the job
   * @param command the program to run (including the params to the program).
   * @return the job ID
   */
  def qsub(name: String, cores: Int, memory: String, command: String): String = {
    runCommand(s"$prefix/qsub -N $name -l select=1:ncpus=$cores:mem=$memory -- $command")
  }

  /**
   * Get statistics for a job
   *
   * @param id the job ID
   * @param opts params for qstat
   * @return output of qstat command
   */
  def qstat(id: String, opts: String): String = {
    try {
      runCommand(s"$prefix/qstat $opts $id")
    } catch {
      case e: java.lang.RuntimeException =>
        ""
    }
  }

  /**
   * Delete a job on a PBS cluster
   *
   * @param id the job ID
   * @param opts params for the qdel
   * @return output of qdel or empty string
   */
  def qdel(id: String, opts: String): String = {
    try {
      runCommand(s"$prefix/qdel $opts $id")
    } catch {
      case e: java.lang.RuntimeException =>
        ""
    }
  }

  /**
   * The famous Unix g/re/p command
   *
   * @param input the buffer to search into
   * @param pattern the pattern to search
   * @return result of the grep command
   */
  def grep(input: String, output: String): String = {
    runCommand(s"grep \'$input\' \'$output\'")
  }

  /**
   * Select specified PBS jobs
   */
  def qselect(): String = {
    try {
      runCommand(s"$prefix/qselect")
    } catch {
      case e: java.lang.RuntimeException =>
        ""
    }
  }
}
