/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.Schedulers;
import com.rshadr.spu11sched.*;

import java.util.HashSet;

final class FixedPriority implements Scheduler {
  private HashSet<Job> readyList;

  public
  FixedPriority ()
  {
    readyList = new HashSet<Job>(67);
  }


  public void
  onActivate (Job job)
  {
  }


  public void
  onTerminated (Job job)
  {
  }


  public void
  schedule ()
  {
  }
}

