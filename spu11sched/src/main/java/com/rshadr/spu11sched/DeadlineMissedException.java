/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

public class DeadlineMissedException extends Exception {
  protected final Job job;
  protected final int curTime;

  protected
  DeadlineMissedException (Job job, int curTime) {
    super("Deadline missed for job "+job.getPriority());
    this.job = job;
    this.curTime = curTime;
  }

}

