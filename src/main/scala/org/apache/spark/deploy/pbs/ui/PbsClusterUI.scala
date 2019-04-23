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

package org.apache.spark.deploy.pbs.ui

import org.apache.spark.ui.{WebUI, SparkUI}
import org.apache.spark.SparkConf
import org.apache.spark.util.Utils
import org.apache.spark.SecurityManager
import org.apache.spark.ui.JettyUtils._

private[spark] class PbsClusterUI(
  securityManager: SecurityManager,
  port: Int,
  val conf: SparkConf,
  dispatcherPublicAddress: String)
extends WebUI(securityManager, securityManager.getSSLOptions("pbs"), port, conf) {

  val killEnabled = conf.getBoolean("spark.ui.killEnabled", true)

  override def initialize() {
    val clusterPage = new PbsClusterPage(this)
    attachPage(clusterPage)
    attachPage(new PbsApplicationPage(this))
    addStaticHandler(SparkUI.STATIC_RESOURCE_DIR)
    attachHandler(createRedirectHandler(
      "/app/kill", "/",  clusterPage.handleAppKillRequest, httpMethods = Set("POST")))
  }
}

private[spark] object PbsClusterUI {
  def main(args: Array[String]) {
    val conf = new SparkConf
    var propertiesFile: String = null
    val port = 8080

    // TODO: Add and parse arguments

    // initialize conf with default properties
    propertiesFile = Utils.loadDefaultSparkProperties(conf, propertiesFile)

    val ui = new PbsClusterUI(new SecurityManager(conf), port, conf, "localhost")
    ui.bind()
    ui.initialize()

    // TODO: daemonize it
    while (true) {
      // do nothing
    }
  }
}
