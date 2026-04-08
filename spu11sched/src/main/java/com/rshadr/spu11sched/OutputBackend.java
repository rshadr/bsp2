/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */

package com.rshadr.spu11sched;

public interface OutputBackend {
  // public void outputTracker (Tracker tracker);

  public static interface Builder {
    public OutputBackend build();
  }
}

