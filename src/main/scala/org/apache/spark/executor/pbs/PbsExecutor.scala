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

package org.apache.spark.executor.pbs

import org.apache.spark.executor.CoarseGrainedExecutorBackend
import org.apache.spark.pbs.Utils

/**
 * Adds the hostname of the current node and starts the executor.
 *
 * The driver is responsible to start executors on a PBS cluster. This is achieved by the driver
 * qsub-ing the executor program. This executor program requires the hostname of the node to be
 * passed as an argument when starting. But since the qsub can land on any node, we do not know
 * the hostname beforehand.
 * This class is just a middleware which is qsub-ed instead of the executor. This then gets the
 * hostname of the node it landed on and then in-turn starts the executor.
 */
private[pbs] object PbsExecutor {
  def main (args: Array[String]) {
    CoarseGrainedExecutorBackend.main("--hostname" +: s"${Utils.runCommand("hostname")}" +: args)
  }
}
