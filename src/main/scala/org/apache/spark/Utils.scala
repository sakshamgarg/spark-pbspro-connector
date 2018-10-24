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

package org.apache.spark.pbs

import scala.sys.process._

private[spark] object Utils {

  val prefix: String = s"/opt/pbs/bin"

  /**
   * Runs a shell command
   *
   * @param command the command to run with the argument
   * @return output of the command
   */
  def runCommand(command: String): String = {
    command.!!.trim()
  }

  /**
   * Submits a job to PBS
   *
   * @param name a name for the job
   * @param cores number of cores for the job
   * @param memory memory to allocate for the job
   * @param command the program to run (including the params to the program).
   * @return the job ID
   */
  def qsub(name: String, cores: Int, memory: String, command: String, env: String = null): String = {
    val envString: String = env match {
      case null =>
        ""
      case "" =>
        ""
      case _ =>
        "-v " + env
    }
    runCommand(s"$prefix/qsub -N $name -l select=1:ncpus=$cores:mem=$memory $envString -- $command")
  }

  /**
   * Get statistics for a job
   *
   * @param id the job ID
   * @param opts params for qstat
   * @return output of qstat command
   */
  def qstat(id: String, opts: String): String = {
    try {
      runCommand(s"$prefix/qstat $opts $id")
    } catch {
      case e: java.lang.RuntimeException =>
        ""
    }
  }

  def qdel(id: String, opts: String): String = {
    try {
      runCommand(s"$prefix/qdel $opts $id")
    } catch {
      case e: java.lang.RuntimeException =>
        ""
    }
  }

  /**
   * The famous Unix g/re/p command
   *
   * @param input the buffer to search into
   * @param pattern the pattern to search
   * @return result of the grep command
   */
  def grep(input: String, output: String): String = {
    runCommand(s"grep \'$input\' \'$output\'")
  }
}
