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
