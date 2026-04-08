/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

public interface Tracker {
  public void onInitialize (Configuration config);
  public void onActiveTick ();

  public static interface Builder {
    public Tracker build();
  };
}

