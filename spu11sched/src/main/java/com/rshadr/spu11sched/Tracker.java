/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

public interface Tracker {
  public void onInitialize (Configuration config);

  public void onJobActivated (int t, Job job);
  public void onJobTerminated (int t, Job job);
  
  public Data onFinish (int t, Exception reason);

  public static interface Data {
  };

  public static interface Builder {
    public Tracker build();
  };
}

