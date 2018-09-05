package org.apache.spark.scheduler.cluster.pbs

import org.apache.spark.executor.PbsExecutorDriver

import org.pbspro.pbs.{PBS, Offer}

private[pbs] class PbsSchedulerDriver() {

  /**
   * Register with pbs and initialize stuff.
   */
  def init() {}

  def reviveOffers() {}
  def acceptOffer(offer: Offer) {}
  def declineOffer(offer: Offer) {}
}
