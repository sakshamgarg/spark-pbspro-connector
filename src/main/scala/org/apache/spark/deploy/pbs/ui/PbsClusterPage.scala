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
import org.apache.spark.deploy.pbs.{PbsApplicationInfo, PbsDriverInfo, PbsServerState}
import org.apache.spark.pbs.Utils

private[ui] class PbsClusterPage(parent: PbsClusterUI) extends WebUIPage("") with Logging {
  val state = new PbsServerState()

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

    Thread.sleep(500)
  }

  def applicationRow(app: PbsApplicationInfo): Seq[Node] = {
    val killLink = {
      val confirm =
        s"if (window.confirm('Are you sure you want to kill app ${app.jobId}?')) " +
          "{ this.parentNode.submit(); return true; } else { return false; }"
      <form action="app/kill/" method="POST" style="display:inline">
        <input type="hidden" name="id" value={ app.jobId.toString } />
        <input type="hidden" name="terminate" value="true" />
        <a href="#" onClick={ confirm } class="kill-link">(kill)</a>
      </form>
    }

    <tr>
      <td>
      {
        if (app.isRunning) {
          <a href={ s"app?appId=${app.jobId}" }>
            { app.jobId }
          </a>
        } else {
          app.jobId
        }
      } { killLink}
      </td>
      <td>{ app.name }</td>
      <td>{ app.cores }</td>
      <td>{ app.mem }</td>
      <td>{ app.submissionDate}</td>
      <td>{ app.user }</td>
      <td>{ app.stateString }</td>
      <td>TODO</td>
    </tr>
  }

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
      <td>{ driver.jobId } { killLink}</td>
      <td>{ driver.submissionDate}</td>
      <td>TODO</td>
      <td>{ driver.stateString }</td>
      <td>{ driver.cores }</td>
      <td>{ driver.mem }</td>
      <td>{ driver.name }</td>
    </tr>
  }

  def render(request: HttpServletRequest): Seq[Node] = {
    val appHeaders = Seq("Application ID", "Name", "Cores", "Memory per Executor",
        "Submission Time", "User", "State", "Duration")
    val driverHeaders = Seq("Submission ID", "Submission Time", "Worker", "State", "Cores",
        "Memory", "Main Class")

    val content =
      <div class="row-fluid">
        <div class="span12">
          <ul class="unstyled">
            <li><strong>Applications:</strong>
              { state.runningApplications.length } <a href="#running-app">running</a>,
              { state.completedApplications.length } <a href="#completed-app">completed</a>
            </li>
            <li><strong>Drivers:</strong>
              { state.runningDrivers.length } <a href="#running-driver">running</a>,
              { state.completedDrivers.length } <a href="#completed-driver">completed</a>
            </li>
            <li><strong>Status:</strong>
              { state.serverStatus }
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
              <a>Running Applications ({ state.runningApplications.length }):</a>
            </h4>
          </span>
          <div class="aggregated-activeApps collapsible-table">
            { UIUtils.listingTable(appHeaders, applicationRow, state.runningApplications) }
          </div>
        </div>
      </div>

      <div class="row-fluid">
        <div class="span12">
          <span id="running-driver" class="collapse-aggregated-activeDrivers collapse-table"
              onClick="collapseTable('collapse-aggregated-activeDrivers', 'aggregated-activeDrivers')">
            <h4>
              <span class="collapse-table-arrow arrow-open"></span>
              <a>Running Drivers ({ state.runningDrivers.length }):</a>
            </h4>
          </span>
          <div class="aggregated-activeDrivers collapsible-table">
            { UIUtils.listingTable(driverHeaders, driverRow, state.runningDrivers) }
          </div>
        </div>
      </div>

      <div class="row-fluid">
        <div class="span12">
          <span id="completed-app" class="collapse-aggregated-completedApps collapse-table"
              onClick="collapseTable('collapse-aggregated-completedApps', 'aggregated-completedApps')">
            <h4>
              <span class="collapse-table-arrow arrow-open"></span>
              <a>Completed Applications ({ state.completedApplications.length }):</a>
            </h4>
          </span>
          <div class="aggregated-completedApps collapsible-table">
            { UIUtils.listingTable(appHeaders, applicationRow, state.completedApplications) }
          </div>
        </div>
      </div>

      <div class="row-fluid">
        <div class="span12">
          <span id="completed-driver" class="collapse-aggregated-completedDrivers collapse-table"
              onClick="collapseTable('collapse-aggregated-completedDrivers', 'aggregated-completedDrivers')">
            <h4>
              <span class="collapse-table-arrow arrow-open"></span>
              <a>Completed Drivers ({ state.completedDrivers.length }):</a>
            </h4>
          </span>
          <div class="aggregated-completedDrivers collapsible-table">
            { UIUtils.listingTable(driverHeaders, driverRow, state.completedDrivers) }
          </div>
        </div>
      </div>

    UIUtils.basicSparkPage(request, content, "Spark Applications on PBS cluster")
  }
}
