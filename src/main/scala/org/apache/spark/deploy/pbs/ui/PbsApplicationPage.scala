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
import org.apache.spark.deploy.pbs.{PbsServerState, PbsApplicationInfo}
import org.apache.spark.pbs.Utils

private[ui] class PbsApplicationPage(parent: PbsClusterUI) extends WebUIPage("app") with Logging {

  def render(request: HttpServletRequest): Seq[Node] = {
    val appId = request.getParameter("appId")
    val app = new PbsApplicationInfo(appId)

    if (app == null) {
      val msg = <div class="row-fluid"> No application with ID {appId} </div>
      return UIUtils.basicSparkPage(request, msg, "Not found")
    }

    val content =
      <div class="row-fluid">
        <div class="span12">
          <ul class="unstyled">
            <li><strong>ID:</strong> { app.id } </li>
            <li><strong>Name:</strong> { app.name } </li>
            <li><strong>User:</strong> { app.user } </li>
            <li><strong>Cores:</strong> { app.cores } </li>
            <li><strong>Executors:</strong> { app.executors } </li>
            <li><strong>Memory:</strong> { app.mem } </li>
            <li><strong>Submit Date:</strong> { app.submissionDate } </li>
            <li><strong>State:</strong> { app.stateString } </li>
          </ul>
        </div>
      </div>
      <div class="row-fluid">
        <div class="span12">
          <pre>
            { Utils.qstat(appId, "-f") }
          </pre><br/>
        </div>
      </div>

    UIUtils.basicSparkPage(request, content, "Application: " + appId)
  }
}
