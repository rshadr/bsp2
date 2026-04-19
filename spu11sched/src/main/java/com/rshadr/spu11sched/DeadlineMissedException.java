/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

public class DeadlineMissedException extends Exception {
  private final Job _job;
  private final int _curTime;

  protected
  DeadlineMissedException (Job job, int curTime) {
    super();
    _job = job;
    _curTime = curTime;
  }

  public Job
  job ()
  {
    return _job;
  }


  public int
  curTime ()
  {
    return _curTime;
  }
}

