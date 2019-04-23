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
        Option(request.getParameter("terminate")).getOrElse("false").toBoolean
      val id = Option(request.getParameter("id"))

      if (id.isDefined && killFlag) {
        action(id.get)
      }
    }

    Thread.sleep(500)
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
      <td>
      {
        if (driver.isRunning) {
          <a href={ s"app?appId=${driver.jobId}" }>
            { driver.jobId }
          </a>
        } else {
          driver.jobId
        }
      } { killLink}
      </td>
      <td>{ driver.submissionDate}</td>
      <td>{ driver.jobName }</td>
      <td>cpus: { driver.ncpus }, mem: { driver.mem }</td>
    </tr>
  }

  def render(request: HttpServletRequest): Seq[Node] = {
    val state = PbsServerState()
    val headers = Seq("Driver ID", "Submission Date", "Main Class", "Driver Resources")

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
            { UIUtils.listingTable(headers, driverRow, state.runningDrivers) }
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
