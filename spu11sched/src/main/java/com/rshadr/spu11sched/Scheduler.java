/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

import java.util.List;

public abstract class Scheduler {
  public static final record Decision(Processor processor, Job job) {}

  public abstract void onActivate (Job job);
  public abstract void onTerminate (Job job);
  public abstract List<Decision> schedule ();

  public static interface Builder {
    public Scheduler build(List<Processor> processors);
  }
}

