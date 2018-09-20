package org.apache.spark.pbs

import scala.sys.process._

private[spark] object Utils {

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
  def qsub(name: String, cores: Int, memory: String, command: String): String = {
    runCommand(s"/opt/pbs/bin/qsub -N $name -l select=1:ncpus=$cores:mem=$memory -- $command")
  }

  /**
   * Get statistics for a job
   *
   * @param id the job ID
   * @param opts params for qstat
   * @return output of qstat command
   */
  def qstat(id: String, opts: String): String = {
    runCommand(s"/opt/pbs/bin/qstat $opts $id")
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
