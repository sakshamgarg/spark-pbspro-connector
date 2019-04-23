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

import org.apache.spark.executor.CoarseGrainedExecutorBackend
import org.apache.spark.pbs.Utils

/**
 * Adds the hostname of the current node and starts the executor.
 *
 * The driver is responsible to start executors on a PBS cluster. This is achieved by the driver
 * qsub-ing the executor program. This executor program requires the hostname of the node to be
 * passed as an argument when starting. But since the qsub can land on any node, we do not know
 * the hostname beforehand.
 * This class is just a middleware which is qsub-ed instead of the executor. This then gets the
 * hostname of the node it landed on and then in-turn starts the executor.
 */
private[pbs] object PbsExecutor {
  def main (args: Array[String]) {
    CoarseGrainedExecutorBackend.main("--hostname" +: s"${Utils.runCommand("hostname")}" +: args)
  }
}
