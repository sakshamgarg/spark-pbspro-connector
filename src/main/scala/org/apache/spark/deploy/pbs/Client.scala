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

import java.io.File

import org.apache.spark.{SparkConf, SparkException}
import org.apache.spark.internal.Logging
import org.apache.spark.pbs.Utils

private[pbs] class Client(args: ClientArguments, sparkConf: SparkConf) extends Logging {

  private val defaultDriverCores: Int = 1
  private val defaultDriverMemory: String = "1G"
  private val defaultAppName: String = "spark-driver"

  /**
   * This is responsible to build and start a driver program on a node in the cluster. This driver
   * then builds the SparkContext and starts the application to which other Executors connect.
   *
   * Our objective is to get the spark-submit to a node in the cluster. This can be achieved if we
   * simply qsub the spark-submit command, but in client mode so that the driver is started on
   * whatever node the qsub went for execution. The driver which will be started will handle the
   * launching of executors on its own.
   */
  def run() {
    // this is only called when in cluster mode which means the spark home should be the executor's
    // spark home
    val sparkHome: String = sparkConf.getOption("spark.pbs.executor.home")
      .orElse(sparkConf.getOption("spark.home"))
      .getOrElse {
        throw new SparkException("Executor Spark home `spark.pbs.executor.home` not set!")
      }
    val appName: String = "sparkjob-" + sparkConf.getOption("spark.app.name")
      .getOrElse(defaultAppName)
      .replaceAll("\\s", "")
      .replaceAll("\\.", "-")
    val driverCores: Int = sparkConf.getOption("spark.driver.cores") match {
      case Some(cores) => cores.toInt
      case None => defaultDriverCores
    }
    val driverMemory: String = sparkConf.getOption("spark.driver.memory")
      .getOrElse(defaultDriverMemory)
    val runScript: String = new File(sparkHome, "bin/spark-submit").getPath

    val opts: String = "--master pbs --deploy-mode client" +
        s" --driver-cores $driverCores --driver-memory $driverMemory" +
        s" --class ${args.mainClass} ${args.primaryJavaResource} ${args.arg}"
        s"" // TODO: Configure the driver command for python, R etc.

    val driverCommand = s"$runScript $opts"

    logInfo(s"Starting driver: $driverCommand")
    Utils.qsub(appName, driverCores, driverMemory, driverCommand)
  }
}
