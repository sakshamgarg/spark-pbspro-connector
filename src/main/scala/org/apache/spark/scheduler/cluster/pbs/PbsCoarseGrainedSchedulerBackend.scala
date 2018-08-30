package org.apache.spark.scheduler.cluster.pbs

import scala.concurrent.Future

import org.apache.spark.{SecurityManager, SparkConf, SparkContext}
import org.apache.spark.scheduler.{TaskScheduler, TaskSchedulerImpl}
import org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend

private[spark] class PbsCoarseGrainedSchedulerBackend(
    scheduler: TaskScheduler,
    sc: SparkContext,
    masterURL: String)
  extends CoarseGrainedSchedulerBackend(scheduler.asInstanceOf[TaskSchedulerImpl], sc.env.rpcEnv) {

  override def start() {
    // TODO
  }

  override def sufficientResourcesRegistered(): Boolean = {
    false
    // TODO
  }

  /* NOTE: Replace all org.apache.mesos thingies with Pbs ones.

  override def offerRescinded(d: org.apache.mesos.SchedulerDriver, o: OfferID) {}

  override def registered(
      driver: org.apache.mesos.SchedulerDriver,
      frameworkId: FrameworkID,
      masterInfo: MasterInfo) {
    // TODO
  }


  override def disconnected(d: org.apache.mesos.SchedulerDriver) {
    // TODO
  }

  override def reregistered(d: org.apache.mesos.SchedulerDriver, masterInfo: MasterInfo) {
    // TODO
  }

  override def resourceOffers(d: org.apache.mesos.SchedulerDriver, offers: JList[Offer]) {
    // TODO
  }

  override def statusUpdate(d: org.apache.mesos.SchedulerDriver, status: TaskStatus) {
    // TODO
  }

  override def error(d: org.apache.mesos.SchedulerDriver, message: String) {
    // TODO
  }
  */

  override def stop() {
    // TODO
  }

  /* NOTE: SlaveID and ExecutorID are the internal ids for mesos. We need something similar for pbs.
  override def frameworkMessage(
      d: org.apache.mesos.SchedulerDriver, e: ExecutorID, s: SlaveID, b: Array[Byte]) {}

  override def slaveLost(d: org.apache.mesos.SchedulerDriver, slaveId: SlaveID): Unit = {
    null
    // TODO
  }

  override def executorLost(
      d: org.apache.mesos.SchedulerDriver, e: ExecutorID, s: SlaveID, status: Int): Unit = {
    null
    // TODO
  }
  */

 /* NOTE: No idea what this is!
  override def applicationId(): String = Option(appId).getOrElse {
    null
    // TODO
  }
  */

  override def doRequestTotalExecutors(requestedTotal: Int): Future[Boolean] = Future.successful {
    false
    // TODO
  }

  override def doKillExecutors(executorIds: Seq[String]): Future[Boolean] = Future.successful {
    false
    // TODO
  }

  override def fetchHadoopDelegationTokens(): Option[Array[Byte]] = {
    null
    // TODO
  }

}
