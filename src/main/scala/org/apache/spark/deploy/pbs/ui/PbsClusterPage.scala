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

import org.apache.spark.internal.Logging
import org.apache.spark.ui.{WebUIPage, UIUtils}
import org.apache.spark.deploy.pbs.{PbsServerState, PbsDriverInfo}
import org.apache.spark.pbs.Utils

private[ui] class PbsClusterPage(parent: PbsClusterUI) extends WebUIPage("") with Logging {

  def handleAppKillRequest(request: HttpServletRequest): Unit = {
    handleKillRequest(request, id => {
      logDebug(s"Killing app with id $id")
      Utils.qdel(id, "")
    })
  }

  private def handleKillRequest(request: HttpServletRequest, action: String => Unit): Unit = {
    if (parent.killEnabled &&
        parent.securityManager.checkModifyPermissions(request.getRemoteUser)) {

      val killFlag =
        Option(UIUtils.stripXSS(request.getParameter("terminate"))).getOrElse("false").toBoolean
      val id = Option(UIUtils.stripXSS(request.getParameter("id")))

      if (id.isDefined && killFlag) {
        action(id.get)
      }
    }

    Thread.sleep(100)
  }

  // TODO: Add link to driver page
  def driverRow(driver: PbsDriverInfo): Seq[Node] = {
    val killLink = {
      val confirm =
        s"if (window.confirm('Are you sure you want to kill driver ${driver.jobId}?')) " +
          "{ this.parentNode.submit(); return true; } else { return false; }"
      <form action="app/kill/" method="POST" style="display:inline">
        <input type="hidden" name="id" value={ driver.jobId.toString } />
        <input type="hidden" name="terminate" value="true" />
        <a href="#" onClick={ confirm } class="kill-link">(kill)</a>
      </form>
    }

    <tr>
      <td>{ driver.jobId } { killLink }</td>
      <td>{ driver.submissionDate}</td>
      <td>{ driver.jobName }</td>
      { if (driver.ncpus != null)
        {
          <td>cpus: { driver.ncpus }, mem: { driver.mem }</td>
        }
      }
    </tr>
  }

  def render(request: HttpServletRequest): Seq[Node] = {
    val state = PbsServerState()
    val headers = Seq("Driver ID", "Submission Date", "Main Class")
    val runningHeaders = Seq("Driver ID", "Submission Date", "Main Class", "Driver Resources")

    val content =
      <div class="row-fluid">
        <div class="span12">
          <ul class="unstyled">
            <li><strong>Status:</strong>
              { state.serverStatus }
            </li>
            <li><strong>Running Applications:</strong>
              <a href="#running-app"> { state.runningDrivers.length } running</a>
            </li>
            <li><strong>Queued Applications:</strong>
              <a href="#queued-app"> { state.queuedDrivers.length } queued</a>
            </li>
            <li><strong>Completed Applications:</strong>
              <a href="#completed-app">{ state.completedDrivers.length } completed</a>
            </li>
          </ul>
        </div>
      </div>

      <div class="row-fluid">
        <div class="span12">
          <span id="running-app" class="collapse-aggregated-activeApps collapse-table"
              onClick="collapseTable('collapse-aggregated-activeApps', 'aggregated-activeApps')">
            <h4>
              <span class="collapse-table-arrow arrow-open"></span>
              <a>Running Applications ({ state.runningDrivers.length }):</a>
            </h4>
          </span>
          <div class="aggregated-activeApps collapsible-table">
            { UIUtils.listingTable(runningHeaders, driverRow, state.runningDrivers) }
          </div>
        </div>
      </div>

      <div class="row-fluid">
        <div class="span12">
          <span id="queued-app" class="collapse-aggregated-queuedApps collapse-table"
              onClick="collapseTable('collapse-aggregated-queuedApps', 'aggregated-queuedApps')">
            <h4>
              <span class="collapse-table-arrow arrow-open"></span>
              <a>Queued Applications ({ state.queuedDrivers.length }):</a>
            </h4>
          </span>
          <div class="aggregated-queuedApps collapsible-table">
            { UIUtils.listingTable(headers, driverRow, state.queuedDrivers) }
          </div>
        </div>
      </div>

      <div class="row-fluid">
        <div class="span12">
          <span id="completed-app" class="collapse-aggregated-completedApps collapse-table"
              onClick="collapseTable('collapse-aggregated-completedApps', 'aggregated-completedApps')">
            <h4>
              <span class="collapse-table-arrow arrow-open"></span>
              <a>Completed Applications ({ state.completedDrivers.length }):</a>
            </h4>
          </span>
          <div class="aggregated-completedApps collapsible-table">
            { UIUtils.listingTable(headers, driverRow, state.completedDrivers) }
          </div>
        </div>
      </div>

    UIUtils.basicSparkPage(request, content, "Spark Applications on PBS cluster")
  }
}
