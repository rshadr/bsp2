/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;
import java.util.List;

public abstract class Tracker {
  public void onInitialize (Configuration config) { };

  public void onJobActivated (int t, Job job) { };
  public void onJobTerminated (int t, Job job) { };

  public void onSchedule (int t, List<Scheduler.Decision> decisions) { };
  
  public abstract Data onFinish (int t, Simulation.Result result);

  public static interface Data {
  };

  public static interface Builder {
    public Tracker build();
  };
}

