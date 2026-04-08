/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */

package com.rshadr.spu11sched;

public interface Scheduler {
  public void onActivate (Job job);
  public void onTerminate (Job job);
  public void schedule ();

  public static interface Builder {
    public Scheduler build();
  }
}

