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

package org.apache.spark.deploy.pbs.ui

import org.apache.spark.ui.{WebUI, SparkUI}
import org.apache.spark.SparkConf
import org.apache.spark.util.Utils
import org.apache.spark.SecurityManager

private[spark] class PbsClusterUI(securityManager: SecurityManager,
  port: Int,
  val conf: SparkConf,
  dispatcherPublicAddress: String)
extends WebUI(securityManager, securityManager.getSSLOptions("pbs"), port, conf) {

  override def initialize() {
    attachPage(new PbsClusterPage(this))
    addStaticHandler(SparkUI.STATIC_RESOURCE_DIR)
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
