/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */

package com.rshadr.spu11sched;
import java.util.List;

public interface OutputBackend {
  // public void outputTracker (Tracker tracker);
  public void outputTrackerDatas (List<Tracker.Data> trackerDatas)
   throws java.io.IOException;

  public static interface Builder {
    public OutputBackend build();
  }
}

