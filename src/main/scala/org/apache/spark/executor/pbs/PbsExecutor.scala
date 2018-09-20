package org.apache.spark.executor.pbs

import org.apache.spark.pbs.Utils
import org.apache.spark.executor.CoarseGrainedExecutorBackend

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
