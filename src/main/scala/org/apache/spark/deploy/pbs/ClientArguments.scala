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

import org.apache.spark.internal.Logging

private[pbs] class ClientArguments(args: Array[String]) extends Logging {

  var mainClass: String = null
  var primaryPyFile: String = null
  var otherPyFiles: String = null
  var primaryRFile: String = null
  var primaryJavaResource: String = null
  var arg: String = null

  parseArgs(args.toList)

  /**
   * Parse arguments for PBS Client
   *
   * @param inputArgs arguments sent by SparkSubmit
   */
  private def parseArgs(inputArgs: List[String]) {
    var args = inputArgs

    while (!args.isEmpty) {
      args match {
        case ("--main-class") :: value :: tail =>
          logInfo(s"main-class: $value")
          mainClass = value
          args = tail

        case ("--primary-py-file") :: value :: tail =>
          logInfo(s"primary-py-file: $value")
          primaryPyFile = value
          args = tail

        case ("--other-py-files") :: value :: tail =>
          logInfo(s"other-py-files: $value")
          otherPyFiles = value
          args = tail

        case ("--primary-r-file") :: value :: tail =>
          logInfo(s"primary-r-file: $value")
          primaryRFile = value
          args = tail

        case ("--primary-java-resource") :: value :: tail =>
          logInfo(s"primary-java-resource: $value")
          primaryJavaResource = value
          args = tail

        case ("--arg") :: value :: tail =>
          logInfo(s"arg: $value")
          arg = value
          args = tail

        case Nil =>

        case _ =>
          throw new IllegalArgumentException(getUsageMessage)
      }
    }

    if (mainClass == null) {
      throw new IllegalArgumentException("Must have a --main-class")
    }

    if (primaryPyFile != null && primaryRFile != null) {
      throw new IllegalArgumentException("Cannot have primary-py-file and primary-r-file at once")
    }
  }

  private def getUsageMessage(): String = {
    "USAGE MESSAGE" // TODO
  }
}
