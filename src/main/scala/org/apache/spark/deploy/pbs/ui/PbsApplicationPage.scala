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
import org.apache.spark.deploy.pbs.PbsDriverInfo
import org.apache.spark.pbs.Utils

private[ui] class PbsApplicationPage(parent: PbsClusterUI) extends WebUIPage("app") with Logging {

  def render(request: HttpServletRequest): Seq[Node] = {
    val appId = UIUtils.stripXSS(request.getParameter("appId"))
    val state = new PbsServerState()
    val app = state.applications.find(_.jobId == appId) match {
      case Some(x) => x
      case None => null
    }

    if (app == null) {
      val msg = <div class="row-fluid"> No application with ID {appId} </div>
      return UIUtils.basicSparkPage(request, msg, "Not found")
    }

    val content =
      <div class="row-fluid">
        <div class="span12">
          <ul class="unstyled">
            <li><strong>ID:</strong> { app.jobId } </li>
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
