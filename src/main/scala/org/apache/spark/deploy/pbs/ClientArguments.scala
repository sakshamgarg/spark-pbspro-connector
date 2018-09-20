package org.apache.spark.deploy.pbs

import org.apache.spark.internal.Logging

private[pbs] class ClientArguments(args: Array[String]) extends Logging {

  var mainClass: String = null
  var primaryPyFile: String = null
  var otherPyFiles: String = null
  var primaryRFile: String = null
  var primaryJavaResource: String = null
  var arg: String = null

  parseArgs(args.toList)

  /**
   * Parse arguments for PBS Client
   *
   * @param inputArgs arguments sent by SparkSubmit
   */
  private def parseArgs(inputArgs: List[String]) {
    var args = inputArgs

    while (!args.isEmpty) {
      args match {
        case ("--main-class") :: value :: tail =>
          logInfo(s"main-class: $value")
          mainClass = value
          args = tail

        case ("--primary-py-file") :: value :: tail =>
          logInfo(s"primary-py-file: $value")
          primaryPyFile = value
          args = tail

        case ("--other-py-files") :: value :: tail =>
          logInfo(s"other-py-files: $value")
          otherPyFiles = value
          args = tail

        case ("--primary-r-file") :: value :: tail =>
          logInfo(s"primary-r-file: $value")
          primaryRFile = value
          args = tail

        case ("--primary-java-resource") :: value :: tail =>
          logInfo(s"primary-java-resource: $value")
          primaryJavaResource = value
          args = tail

        case ("--arg") :: value :: tail =>
          logInfo(s"arg: $value")
          arg = value
          args = tail

        case Nil =>

        case _ =>
          throw new IllegalArgumentException(getUsageMessage)
      }
    }

    if (mainClass == null)
      throw new IllegalArgumentException("Must have a --main-class")

    if (primaryPyFile != null && primaryRFile != null)
      throw new IllegalArgumentException("Cannot have primary-py-file and primary-r-file at once")
  }

  private def getUsageMessage(): String = {
    "USAGE MESSAGE" // TODO
  }
}
