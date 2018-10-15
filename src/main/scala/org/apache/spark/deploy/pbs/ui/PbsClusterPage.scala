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

import javax.servlet.http.HttpServletRequest
import scala.xml.Node

import org.apache.spark.ui.{WebUIPage, UIUtils}
import org.apache.spark.deploy.pbs.{PbsServerState, PbsDriverInfo}

private[ui] class PbsClusterPage(parent: PbsClusterUI) extends WebUIPage("") {

  // TODO: Add link to driver page
  def driverRow(driver: PbsDriverInfo): Seq[Node] = {
    <tr>
      <td>{ driver.jobId }</td>
      <td>{ driver.submissionDate}</td>
      <td>{ driver.jobName }</td>
      <td>cpus: { driver.ncpus }, mem: { driver.mem }</td>
    </tr>
  }

  def render(request: HttpServletRequest): Seq[Node] = {
    val serverState = PbsServerState()
    val headers = Seq("Driver ID", "Submission Date", "Main Class", "Driver Resources")
    val content =
      <div class="row-fluid">
        <div class="span12">
          <ul class="unstyled">
            <li><strong>Status:</strong> { serverState.serverStatus } </li>
          </ul>
        </div>
      </div>
      <div class="row-fluid">
        <div class="span12">
          <h4>Running Drivers:</h4>
          {
            UIUtils.listingTable(headers, driverRow,
              serverState.drivers.filter(_.state == "R").toList)
          }
          <h4>Queued Drivers:</h4>
          {
            UIUtils.listingTable(headers, driverRow,
              serverState.drivers.filter(_.state == "Q").toList)
          }
        </div>
      </div>
    UIUtils.basicSparkPage(request, content, "Spark Drivers for PBS cluster")
  }
}
