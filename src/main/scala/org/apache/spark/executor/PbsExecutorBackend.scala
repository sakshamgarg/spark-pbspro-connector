package org.apache.spark.executor

import java.nio.ByteBuffer

import org.apache.spark.{SparkConf, SparkEnv, TaskState}
import org.apache.spark.internal.Logging

private[spark] class PbsExecutorBackend extends ExecutorBackend with Logging {

  override def statusUpdate(taskId: Long, state: TaskState.TaskState, data: ByteBuffer) {
    // TODO
  }

  /* TODO
  override def registered(
      driver: ExecutorDriver,
      executorInfo: ExecutorInfo,
      frameworkInfo: FrameworkInfo,
      slaveInfo: SlaveInfo) {
  }

  override def launchTask(d: ExecutorDriver, taskInfo: TaskInfo) {}

  override def error(d: ExecutorDriver, message: String) {}

  override def killTask(d: ExecutorDriver, t: TaskID) {}

  override def reregistered(d: ExecutorDriver, p2: SlaveInfo) {}

  override def disconnected(d: ExecutorDriver) {}

  override def frameworkMessage(d: ExecutorDriver, data: Array[Byte]) {}

  override def shutdown(d: ExecutorDriver) {}

  */
}

/*
 * TODO: Mesos has an object which is the "entry point" for executor, as follows:
 *    private[spark] object MesosExecutorBackend extends Logging {
 *
 * Find out what has to be done with this shit.
 */
